package gr.hua.dit.petcare.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Το αντικείμενο αυτό χρησιμοποιείται από τον adapter για να καλέσει
 * την εξωτερική υπηρεσία ειδοποιήσεων (Notification Service).
 */
public record SendSmsRequest(

        // Αριθμός τηλεφώνου σε διεθνή μορφή (π.χ. +3069...)
        // Δεν επιτρέπεται να είναι null ή κενό
        @NotNull
        @NotBlank
        String e164,

        // Κείμενο του SMS που θέλουμε να σταλεί
        // Επίσης δεν επιτρέπεται να είναι null ή κενό
        @NotNull
        @NotBlank
        String content

) {
}
