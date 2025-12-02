package gr.hua.dit.petcare.notification.port;

/**
 * διεπαφή της εφαρμογής PetCare για αποστολή SMS.
 *
 * Η υπόλοιπη εφαρμογή δεν γνωρίζει πώς υλοποιείται η αποστολή
 * Απλώς καλεί αυτή τη μέθοδο και περιμένει μια απάντηση true/false.
 */
public interface SmsNotificationPort {

    /**
     * Στέλνει SMS μέσω της εξωτερικής υπηρεσίας ειδοποιήσεων.
     *
     * @param phoneE164 τηλέφωνο παραλήπτη σε διεθνή μορφή
     * @param content   κείμενο του SMS που θα σταλεί
     * @return true αν η αποστολή θεωρηθεί επιτυχής, false διαφορετικά
     */
    boolean sendSms(String phoneE164, String content);
}
