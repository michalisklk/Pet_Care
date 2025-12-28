package gr.hua.dit.petcare.notification.port;

import gr.hua.dit.petcare.notification.dto.PhoneNumberValidationResult;

/**
 * PORT συμβόλαιο της PetCare για έλεγχο τηλεφώνου.
 * Η PetCare θέλει να μπορεί να ελέγχει αν ένα τηλέφωνο είναι σωστό (valid)
 * και να το μετατρέπει σε E.164 μορφή (+30...).
 * Δεν μας νοιάζει ποια εξωτερική υπηρεσία το κάνει αυτό.
 * Αρκεί να έχουμε αυτό το interface και να υπάρχει κάπου μια υλοποίηση (adapter)
 * που θα καλεί το εξωτερικό service.
 * Έτσι η PetCare μένει ανεξάρτητη και βλέπει την εξωτερική υπηρεσία σαν black box.
 */
public interface PhoneNumberValidationPort {

    /**
     * Παίρνει ένα raw τηλέφωνο (π.χ. 6940000000 ή +30 694...)
     * και επιστρέφει αποτέλεσμα:
     * - valid = true/false
     * - e164 = +30694... (αν είναι valid)
     */
    PhoneNumberValidationResult validate(String rawPhoneNumber);
}
