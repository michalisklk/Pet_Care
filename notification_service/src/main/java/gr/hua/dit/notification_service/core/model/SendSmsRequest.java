package gr.hua.dit.notification_service.core.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 *  Αίτημα (DTO) για αποστολή SMS.
 * Περιλαμβάνει:
 * - e164 : αριθμό τηλεφώνου σε διεθνή μορφή (π.χ. +3069...)
 * - content : το κείμενο του SMS έως 160 χαρακτήρες
 */
public record SendSmsRequest(

        @NotBlank(message = "e164 is required")
        @Pattern(
                regexp = "^\\+[1-9]\\d{7,14}$",
                message = "e164 must be in E.164 format, e.g. +306944991291"
        )
        String e164,

        @NotBlank(message = "content is required")
        @Size(max = 160, message = "content must be at most 160 characters")
        String content

) {}
