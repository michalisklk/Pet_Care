package gr.hua.dit.petcare.notification.dto;

/**
 * DTO (Data Transfer Object) κουτί δεδομένων που χρησιμοποιούμε
 * για να διαβάσουμε ή να κρατήσουμε την απάντηση (JSON) από την εξωτερική υπηρεσία.
 */
public record PhoneNumberValidationResult(
        // Ο αριθμός όπως τον έδωσε ο χρήστης
        String raw,

        // true αν το τηλέφωνο θεωρείται έγκυρο, αλλιώς false
        boolean valid,

        // Π.χ. "mobile" (κινητό) ή "fixed_line"
        String type,

        // Ο αριθμός σε διεθνή μορφή E.164, π.χ. +30694, αν είναι valid
        String e164
) {}
