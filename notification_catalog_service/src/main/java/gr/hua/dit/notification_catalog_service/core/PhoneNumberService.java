package gr.hua.dit.notification_catalog_service.core;

import gr.hua.dit.notification_catalog_service.core.model.PhoneNumberValidationResult;

/**
 * Αυτό είναι για έλεγχο τηλεφώνων μέσα στην εξωτερική υπηρεσία.
 * - του δίνουμε ένα τηλέφωνο όπως το γράφει ο χρήστης (raw),
 * - και μας λέει αν είναι έγκυρο και μας το δίνει σε μορφή E.164 (+30...),
 */
public interface PhoneNumberService {

    /**
     * Έλεγχος και κανονικοποίηση τηλεφώνου.
     *
     * @param rawPhoneNumber το τηλέφωνο όπως το παίρνουμε ωμό (π.χ. 694..., +30..., με κενά κλπ)
     * @return αποτέλεσμα με:
     *         - valid (true/false)
     *         - e164 (π.χ. +30694...) αν είναι valid
     *         - και κάποια extra πληροφορία (π.χ. type), αν υπάρχει
     */
    PhoneNumberValidationResult validatePhoneNumber(String rawPhoneNumber);
}
