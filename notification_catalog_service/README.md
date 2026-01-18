# Notification Service (SMS) — PetCare External Service

Ανεξάρτητη Spring Boot μικροϋπηρεσία που παρέχει:
- **Phone validation/normalization** σε μορφή **E.164** (libphonenumber, default region: **GR**)
- **Αποστολή SMS** μέσω **mock provider** ή **Routee provider**

Τρέχει αυτόνομα στο **port 8081** και καταναλώνεται από την PetCare μέσω REST.

---

## Run

**Windows**
```bash
cd notification_service
.\mvnw.cmd clean spring-boot:run
```

**Mac/Linux**
```bash
cd notification_service
./mvnw clean spring-boot:run
```

Base URL: `http://localhost:8081`

---

## Swagger UI
- `http://localhost:8081/swagger-ui.html`
- (εναλλακτικά) `http://localhost:8081/swagger-ui/index.html`

---

## API (v1)

### 1) Validate phone number
`GET /api/v1/phone-numbers/{phoneNumber}/validations`

Example:
```bash
curl http://localhost:8081/api/v1/phone-numbers/6940000000/validations
```

Response (example):
```json
{
  "raw": "6940000000",
  "valid": true,
  "type": "MOBILE",
  "e164": "+306940000000"
}
```

### 2) Send SMS
`POST /api/v1/sms`

Example:
```bash
curl -X POST http://localhost:8081/api/v1/sms \
  -H "Content-Type: application/json" \
  -d '{"e164":"+306940000000","content":"PetCare: Test message"}'
```

Response:
```json
{ "sent": true }
```

---

## Provider selection (Mock vs Routee)

Η υπηρεσία επιλέγει provider δυναμικά:
- Αν **λείπουν** τα Routee credentials --> χρησιμοποιείται **MockSmsService**
- Αν **υπάρχουν** --> χρησιμοποιείται **RouteeSmsService**

Properties (application.yaml):
- `routee.app-id`
- `routee.app-secret`
- `routee.sender`

---
