# notification_catalog_service

Εξωτερική μικροϋπηρεσία (Spring Boot) που παρέχει **SMS notifications**, **phone validation/normalization (E.164)** και **mock vaccine catalog** για την κεντρική εφαρμογή **PetCare** μέσω REST/HTTP.

---

## Περιεχόμενα
- [Τι κάνει](#τι-κάνει)
- [Endpoints (REST API Contract)](#endpoints-rest-api-contract)
- [Σχήμα σφαλμάτων (ApiError)](#σχήμα-σφαλμάτων-apierror)
- [Monitor UI (debug)](#monitor-ui-debug)
- [Swagger / OpenAPI](#swagger--openapi)
- [Παραμετροποίηση (applicationyaml)](#παραμετροποίηση-applicationyaml)
- [Τρέξιμο τοπικά](#τρέξιμο-τοπικά)
- [Mock vs Routee](#mock-vs-routee)
- [Ενσωμάτωση με PetCare](#ενσωμάτωση-με-petcare)
- [Troubleshooting](#troubleshooting)
- [Καθαρισμός project](#καθαρισμός-project)

---

## Τι κάνει
Η υπηρεσία εκθέτει τα παρακάτω:
1. **Phone validation & normalization** με Google `libphonenumber` (default region: `GR`)
    - Επιστρέφει αν ένας αριθμός είναι έγκυρος και (αν γίνεται) τον κανονικοποιεί σε **E.164**.
2. **Αποστολή SMS**
    - Σε **Mock mode** (για δοκιμές) ή
    - Πραγματική αποστολή μέσω **Routee** (με credentials).
3. **Vaccine catalog (mock helper API)**
    - In-memory λίστα εμβολίων ανά `species` (για demo στο UI της PetCare).
4. **Monitor UI**
    - Σελίδα/endpoint για να βλέπεις τα τελευταία SMS events (in-memory), χρήσιμο για debugging.

---

## Endpoints (REST API Contract)

### 1) Αποστολή SMS
**POST** `/api/v1/sms`

**Request (JSON)**
```json
{
  "e164": "+3069XXXXXXXX",
  "content": "Your message (max 160 chars)"
}
```

Κανόνες:
- `e164` πρέπει να είναι E.164 (π.χ. `+3069...`)
- `content` ≤ **160** χαρακτήρες

**Response**
```json
{ "sent": true }
```
ή
```json
{ "sent": false }
```

> Σημείωση: αν ο provider αποτύχει (π.χ. Routee 400/401/timeout), η υπηρεσία επιστρέφει συνήθως `sent=false` (best-effort), ενώ τα validation errors επιστρέφουν `ApiError` (400).

---

### 2) Phone validation / normalization
**GET** `/api/v1/phone-numbers/validations?phone={phoneNumber}`

**Response**
```json
{
  "raw": "6944....",
  "valid": true,
  "type": "mobile",
  "e164": "+306944...."
}
```

Όταν είναι invalid:
```json
{
  "raw": "123",
  "valid": false,
  "type": null,
  "e164": null
}
```

> Χρησιμοποιεί `libphonenumber` με **default region `GR`** για σωστό parse/format όπου είναι εφικτό.

---

### 3) Vaccine catalog (helper API – mock)
**GET** `/api/v1/vaccines/{species}`

**Response (wrapper)**
```json
{
  "species": "DOG",
  "vaccines": [
    { "name": "...", "recommendedAgeMonths": 2, "notes": "..." }
  ]
}
```

**GET** `/api/v1/vaccines/supported-species`

**Response**
```json
["DOG","CAT", "..."]
```

Για μη υποστηριζόμενο `species` επιστρέφεται **400** με `ApiError` και μήνυμα που προτείνει να γίνει χρήση του endpoint `supported-species`.

---

## Σχήμα σφαλμάτων (ApiError)
Σε validation/parse/type errors η υπηρεσία επιστρέφει τυποποιημένο JSON:



- `fieldErrors` εμφανίζεται **όπου χρειάζεται** (validation).
- Για απρόβλεπτα σφάλματα υπάρχει fallback **500** με γενικό μήνυμα `"Unexpected server error"`.

---

## Monitor UI (debug)
- **GET** `/` → σερβίρει `monitor.html` (Thymeleaf)
- Η σελίδα κάνει polling στα:
    - **GET** `/api/v1/monitor/sms-events?limit=50`
    - **POST** `/api/v1/monitor/sms-events/clear`

> Το monitor είναι in-memory και χρήσιμο για debugging. 

---

## Swagger / OpenAPI
- Swagger UI: `/swagger-ui.html`
- OpenAPI docs: `/v3/api-docs`

Υπάρχει OpenAPI grouping `"notification-api"` για endpoints κάτω από `/api/v1/**` (sms/phone/vaccines).

---

## Παραμετροποίηση (application.yaml)

### Βασικά
```yaml
server:
  port: 8081
```

### Routee (για real SMS)
```yaml
routee:
  app-id:
  app-secret:
  sender: 
```

### Cache (token caching)
```yaml
spring:
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=1,expireAfterWrite=10m
```



---

## Τρέξιμο τοπικά

### Προαπαιτούμενα
- Java **21**
- Maven (ή Maven Wrapper αν υπάρχει στο project)

### Εκτέλεση
Από τον φάκελο του project:

**Linux/macOS**
```bash
mvn clean spring-boot:run
# ή (αν υπάρχει wrapper)
./mvnw clean spring-boot:run
```

**Windows (PowerShell)**
```powershell
mvn clean spring-boot:run
# ή (αν υπάρχει wrapper)
.\mvnw.cmd clean spring-boot:run
```

Μετά:
- Swagger: `http://localhost:8081/swagger-ui.html`
- Monitor: `http://localhost:8081/`

---

## Mock vs Routee
Η υπηρεσία επιλέγει provider αυτόματα:
- **RouteeSmsService** όταν υπάρχουν `routee.app-id`, `routee.app-secret`, `routee.sender`
- **MockSmsService** όταν λείπει κάποιο από τα παραπάνω (ιδανικό για δοκιμές)

Σε Mock mode:
- Το endpoint `/api/v1/sms` επιστρέφει `{"sent": true}` και γράφει event στο monitor store.

---

## Ενσωμάτωση με PetCare
Η PetCare καταναλώνει το external API ως “black box”:
- Phone validation → για να προκύψει σωστό E.164
- Send SMS → best effort ειδοποίηση
- Vaccines → helper endpoint για λίστα


---

## Troubleshooting

### 1) Routee επιστρέφει 400/401
- Έλεγξε `ROUTEE_APP_ID`, `ROUTEE_APP_SECRET`, `ROUTEE_SENDER`.
- Έλεγξε ότι το `e164` είναι σωστό (π.χ. `+3069...`).

### 2) Phone validation βγάζει `valid=false`
- Δοκίμασε να στείλεις αριθμό σε μορφή GR κινητού (π.χ. `69...`) ή ήδη σε `+30...`.
- Το `e164` θα είναι `null` όταν είναι invalid.

### 3) Δεν φαίνονται SMS events στο Monitor
- Αν καλείς Routee και αποτυγχάνει, μπορεί να καταγράφεται event με `sent=false`.
- Σε Mock mode καταγράφονται πάντα.

---


