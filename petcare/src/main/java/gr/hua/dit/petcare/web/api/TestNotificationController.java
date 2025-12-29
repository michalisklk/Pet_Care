package gr.hua.dit.petcare.web.api;

import gr.hua.dit.petcare.notification.dto.PhoneNumberValidationResult;
import gr.hua.dit.petcare.notification.port.PhoneNumberValidationPort;
import gr.hua.dit.petcare.notification.port.SmsNotificationPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Αυτός ο controller υπάρχει ΚΑΘΑΡΑ για δοκιμές (test).
 * Δηλαδή: πριν το "κουμπώσουμε" κανονικά στη ροή ραντεβού,
 * μπορούμε να δοκιμάζουμε ότι η PetCare μπορεί να καλέσει
 * την εξωτερική υπηρεσία (Notification Service) σαν black box.
 */
@RestController
@RequestMapping("/api/test-notifications")
public class TestNotificationController {

    // Ports interfaces. Η PetCare δεν ξέρει υλοποίηση, μόνο τι ζητά.
    private final SmsNotificationPort smsNotificationPort;
    private final PhoneNumberValidationPort phoneNumberValidationPort;

    /**
     * Constructor injection:
     * Ο Spring φτιάχνει τα beans και μας τα δίνει εδώ.
     * (π.χ. πίσω από τα ports υπάρχουν adapters που κάνουν HTTP calls)
     */
    public TestNotificationController(
            SmsNotificationPort smsNotificationPort,
            PhoneNumberValidationPort phoneNumberValidationPort
    ) {
        this.smsNotificationPort = smsNotificationPort;
        this.phoneNumberValidationPort = phoneNumberValidationPort;
    }

    /**
     * Test endpoint:
     * POST /api/test-notifications/sms?phone=...&content=...
     *
     * Βήματα:
     * 1) Κάνουμε validate το τηλέφωνο (external service)
     * 2) Αν είναι έγκυρο, στέλνουμε SMS με E.164 μορφή (external service)
     */
    @PostMapping("/sms")
    public ResponseEntity<String> testSms(
            @RequestParam String phone,
            @RequestParam String content
    ) {
        // για να μη μας χαλάσουν spaces
        phone = phone == null ? "" : phone.trim();
        content = content == null ? "" : content.trim();

        if (phone.isEmpty()) {
            return ResponseEntity.badRequest().body("Λείπει το phone.");
        }
        if (content.isEmpty()) {
            return ResponseEntity.badRequest().body("Λείπει το content.");
        }

        // Καλούμε την εξωτερική υπηρεσία για validation/normalization τηλεφώνου
        PhoneNumberValidationResult validation = phoneNumberValidationPort.validate(phone);

        // Αν κάτι πάει στραβά ή το τηλέφωνο δεν είναι valid, δεν προχωράμε σε SMS
        if (validation == null || !validation.valid() || validation.e164() == null) {
            return ResponseEntity.badRequest().body("Μη έγκυρο τηλέφωνο: " + phone);
        }

        // Στέλνουμε SMS στο normalized E.164 (π.χ. +30694...)
        boolean sent = smsNotificationPort.sendSms(validation.e164(), content);

        // Επιστρέφουμε απάντηση για να βλέπουμε τι έγινε στο test
        return ResponseEntity.ok("SMS sent=" + sent + " | e164=" + validation.e164());
    }
}
