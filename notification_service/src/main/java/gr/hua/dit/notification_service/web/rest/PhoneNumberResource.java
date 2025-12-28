package gr.hua.dit.notification_service.web.rest;

import gr.hua.dit.notification_service.core.PhoneNumberService;
import gr.hua.dit.notification_service.core.model.PhoneNumberValidationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Αυτός ο controller ανήκει στην ΕΞΩΤΕΡΙΚΗ υπηρεσία
 * - Να δέχεται ένα τηλέφωνο (όπως το δίνει ο χρήστης),
 * - να το ελέγχει αν είναι έγκυρο,
 * - και να το επιστρέφει σε "κανονική" μορφή E.164 (+30...).
 *
 * Η κύρια εφαρμογή (PetCare) το καλεί σαν black box μέσω HTTP.
 */
@RestController
@RequestMapping("/api/v1/phone-numbers")
public class PhoneNumberResource {

    // Service που κάνει validation με libphonenumber κλπ
    private final PhoneNumberService phoneNumberService;

    /**
     * Constructor injection:
     * Ο Spring μας δίνει εδώ το PhoneNumberService.
     */
    public PhoneNumberResource(PhoneNumberService phoneNumberService) {
        this.phoneNumberService = phoneNumberService;
    }

    /**
     * Endpoint:
     * GET /api/v1/phone-numbers/{phoneNumber}/validations
     *
     * Παράδειγμα:
     * GET /api/v1/phone-numbers/6940000000/validations
     */
    @GetMapping("/{phoneNumber}/validations")
    public ResponseEntity<PhoneNumberValidationResult> phoneNumberValidation(
            @PathVariable String phoneNumber
    ) {
        // Καλούμε το service για να κάνει validate ή normalize
        PhoneNumberValidationResult result = phoneNumberService.validatePhoneNumber(phoneNumber);

        // Επιστρέφουμε 200 OK και το αποτέλεσμα σαν JSON
        return ResponseEntity.ok(result);
    }
}
