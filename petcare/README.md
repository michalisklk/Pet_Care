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
