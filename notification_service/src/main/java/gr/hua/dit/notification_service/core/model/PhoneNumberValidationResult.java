package gr.hua.dit.notification_service.core.model;

/**
 * DTO (Data Transfer Object) κουτάκι δεδομένων που επιστρέφουμε σαν JSON
 * από την εξωτερική υπηρεσία όταν κάνουμε validation σε ένα τηλέφωνο.
 */
public record PhoneNumberValidationResult(
        String raw,
        boolean valid,
        String type,
        String e164
) {

    /**
     * Helper method για άκυρο αποτέλεσμα.
     * όταν:
     * - το τηλέφωνο δεν είναι valid
     * - ή δεν μπορούμε να το διαβάσουμε σωστά
     */
    public static PhoneNumberValidationResult invalid(final String raw) {
        return new PhoneNumberValidationResult(raw, false, null, null);
    }

    /**
     * Helper method για έγκυρο αποτέλεσμα.
     * όταν:
     * - το τηλέφωνο είναι valid
     * - και έχουμε έτοιμη την E.164 μορφή του (+30...)
     */
    public static PhoneNumberValidationResult valid(final String raw, final String type, final String e164) {
        return new PhoneNumberValidationResult(raw, true, type, e164);
    }
}
