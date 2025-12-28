package gr.hua.dit.notification_service.core.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Αίτημα (DTO) για αποστολή SMS.
 * Περιλαμβάνει:
 * - e164  : αριθμό τηλεφώνου σε διεθνή μορφή (π.χ. +3069...)
 * - content : το κείμενο του SMS
 */
public record SendSmsRequest(
        // Αριθμός τηλεφώνου σε μορφή E.164 (π.χ. +3069...)
        // Δεν επιτρέπεται να είναι null ή κενός
        @NotNull
        @NotBlank
        String e164,

        // Κείμενο του SMS που θέλουμε να στείλουμε
        // Επίσης δεν επιτρέπεται να είναι null ή κενό
        @NotNull
        @NotBlank
        String content
) {
}
