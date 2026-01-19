# PetCare (Spring Boot)

Web εφαρμογή για διαχείριση κατοικιδίων και ραντεβού σε κτηνίατρο, με:
- UI (Thymeleaf) για ιδιοκτήτες/κτηνιάτρους (cookie/session auth)
- REST API (JSON) για διαλειτουργικότητα (JWT auth)
- Χρήση εξωτερικής υπηρεσίας (black-box) για phone validation & SMS notifications μέσω REST client (ports/adapters)
- Swagger / OpenAPI τεκμηρίωση για το REST API

---

## 1) Τεχνολογίες
- Java 21
- Spring Boot (Web, Security, Data JPA, Validation, Thymeleaf)
- H2 Database (file-based)
- JWT (io.jsonwebtoken)
- Swagger / OpenAPI (springdoc)

---

## 2) Ρόλοι & Λειτουργικότητες

### Ρόλοι
- **PET_OWNER (Ιδιοκτήτης)**
- **VET (Κτηνίατρος)**

### PET_OWNER
- Καταχώριση/διαχείριση κατοικιδίων (CRUD, soft delete)
- Δημιουργία ραντεβού (PENDING)
- Προβολή των δικών του ραντεβού

### VET
- Προβολή ραντεβού που του ανήκουν
- Confirm / Cancel / Complete ραντεβού (+ notes)

---

## 3) Business Rules (ενδεικτικά)
- **No overlapping appointments** για τον ίδιο vet (όχι ραντεβού πάνω σε άλλο)
- **Διάρκεια ραντεβού:** 30 λεπτά
- **Start time:** τουλάχιστον 1 ώρα από το “τώρα”
- **Owner authorization:** ο owner βλέπει/πειράζει μόνο τα δικά του pets/ραντεβού
- **Cooldown για “σημαντικά” reasons** (π.χ. VACCINES / SURGERY / DIET) με βάση το τελευταίο σχετικό ραντεβού

---

## 4) Αρχιτεκτονική / Πακέτα

- `gr.hua.dit.petcare.core`
    - `model` (JPA entities: Person, Pet, Appointment)
    - `repository` (JPA repositories)
    - `service` (business logic)
    - `dto` (DTOs)
- `gr.hua.dit.petcare.web`
    - `ui` (Thymeleaf MVC Controllers)
    - `api` (REST Controllers + DTO responses + mapper + exception handler)
- `gr.hua.dit.petcare.security`
    - JWT Service + JWT Filter
- `gr.hua.dit.petcare.notification`
    - `port` (interfaces/ports)
    - `adapter` (HTTP clients προς εξωτερική υπηρεσία)
    - `dto` (DTOs της εξωτερικής υπηρεσίας)
- `gr.hua.dit.petcare.config`
    - Security config, OpenAPI config,App config,Rest client,dev data config, CustomAuthenticationSuccessHandler config

---

## 5) Προαπαιτούμενα
- Java 21
- Maven (ή Maven Wrapper)
- Ελεύθερα ports:
    - PetCare: **8080**
    - (Εξωτερική υπηρεσία SMS/Phone Validation: **8081**, πρέπει να τρέχει ξεχωριστά)

---

## 6) Εκτέλεση
Από φάκελο `petcare/`:

```powershell
.\mvnw.cmd spring-boot:run 

```

### 6.1) Εκτέλεση (πλήρης ροή – Windows)
1) **Τρέχουμε πρώτα** την εξωτερική υπηρεσία (`notification_catalog_service`) στο port **8081**:
```powershell
cd notification_catalog_service
.\mvnw.cmd clean spring-boot:run
```

2) Μετά τρέχουμε την PetCare στο port **8080**:
```powershell
cd petcare
.\mvnw.cmd clean spring-boot:run
```

### 6.2) Εκτέλεση (Linux/macOS)
```bash
cd notification_catalog_service
./mvnw clean spring-boot:run
```
και σε άλλο terminal:
```bash
cd petcare
./mvnw clean spring-boot:run
```

> Αν δεν υπάρχει Maven Wrapper, χρησιμοποιούμε`mvn clean spring-boot:run`.

### 6.3) Έλεγχος ότι “τρέχει”
- PetCare UI: `http://localhost:8080/`
- PetCare Swagger: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console`
- External Swagger: `http://localhost:8081/swagger-ui.html`
- External Monitor UI: `http://localhost:8081/`

---

## 7) Ρυθμίσεις (application.yml) – External services & DB
Η PetCare καλεί την εξωτερική υπηρεσία μέσω των παρακάτω properties:

```yaml
external:
  sms-service:
    url: http://localhost:8081/api/v1/sms
  phone-service:
    url: http://localhost:8081/api/v1/phone-numbers
  vaccines-service:
    url: http://localhost:8081/api/v1/vaccines
```

**Phone validation endpoint που καλείται από την PetCare**
- `GET {external.phone-service.url}/validations?phone=...`

**H2 DB (file-based)**
- `jdbc:h2:file:./LOCAL_DATA/h2/app`
- Με `spring.jpa.hibernate.ddl-auto=create-drop` η βάση φτιάχνεται σε κάθε run και στο τέλος σβήνεται.

---

## 8) Demo χρήστες (DevDataConfig)
Με το `DevDataConfig` δημιουργούνται demo accounts (password: **password**):

- **PET_OWNER**
    - `owner1@gmail.com`
    - `owner2@gmail.com`
- **VET**
    - `vet1@gmail.com`
    - `vet2@gmail.com`

---

## 9) REST API Authentication (JWT)
- **Login (API):** `POST /api/v1/auth/login`
    - body: `{ "email": "...", "password": "..." }`
    - response: `{ "token": "..." }`
- Για protected endpoints του API βάλε header:
    - `Authorization: Bearer <token>`
- Public endpoints: `/api/v1/auth/**`
- UI παραμένει **session-based** (form login + cookie).


