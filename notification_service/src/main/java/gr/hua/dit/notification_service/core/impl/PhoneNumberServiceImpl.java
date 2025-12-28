package gr.hua.dit.notification_service.core.impl;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import gr.hua.dit.notification_service.core.PhoneNumberService;
import gr.hua.dit.notification_service.core.model.PhoneNumberValidationResult;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Υλοποίηση του PhoneNumberService με τη βιβλιοθήκη Google libphonenumber.
 * Παίρνει ένα τηλέφωνο όπως το γράφει ο χρήστης (raw)
 * Προσπαθεί να το διαβάσει σαν τηλέφωνο Ελλάδας (GR)
 * Αν είναι έγκυρο:
 *    - βρίσκει τύπο (κινητό/σταθερό κλπ)
 *    - το μετατρέπει σε διεθνή μορφή E.164 (+30...)
 * Επιστρέφει αποτέλεσμα (valid/invalid)
 */
@Service
public class PhoneNumberServiceImpl implements PhoneNumberService {

    private final PhoneNumberUtil phoneNumberUtil;

    // Default χώρα για parse (εδώ Ελλάδα)
    private final String defaultRegion;

    public PhoneNumberServiceImpl() {
        this.phoneNumberUtil = PhoneNumberUtil.getInstance();

        // Για την εργασία μας θεωρούμε ότι τα τηλέφωνα είναι ελληνικά,
        // οπότε default region = GR.
        this.defaultRegion = "GR";
    }

    @Override
    public PhoneNumberValidationResult validatePhoneNumber(String rawPhoneNumber) {

        // Αν μας έρθει null ή κενό, το θεωρούμε άκυρο
        if (rawPhoneNumber == null || rawPhoneNumber.isBlank()) {
            return PhoneNumberValidationResult.invalid(rawPhoneNumber);
        }

        try {
            // Προσπαθούμε να μετατρέψουμε το raw input σε "PhoneNumber" αντικείμενο.
            // Η defaultRegion ("GR") βοηθάει όταν ο χρήστης γράφει π.χ. 694... χωρίς +30.
            Phonenumber.PhoneNumber parsed = phoneNumberUtil.parse(rawPhoneNumber, defaultRegion);

            // Έλεγχος εγκυρότητας:
            // αν δεν είναι πραγματικό και έγκυρο τηλέφωνο τότε invalid
            if (!phoneNumberUtil.isValidNumber(parsed)) {
                return PhoneNumberValidationResult.invalid(rawPhoneNumber);
            }

            // Επιπλέον προεραιτικός έλεγχος για Ελλάδα
            // Δηλαδή μπορεί να είναι valid γενικά, αλλά όχι για GR.
            if (!phoneNumberUtil.isValidNumberForRegion(parsed, defaultRegion)) {
                return PhoneNumberValidationResult.invalid(rawPhoneNumber);
            }

            // Τύπος αριθμού (π.χ. mobile, fixed_line κλπ)
            String type = phoneNumberUtil.getNumberType(parsed).name().toLowerCase(Locale.ROOT);

            // Κανονικοποίηση σε E.164 (π.χ. +30694...)
            String e164 = phoneNumberUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164);

            // Επιστρέφουμε valid αποτέλεσμα
            return PhoneNumberValidationResult.valid(rawPhoneNumber, type, e164);

        } catch (NumberParseException e) {
            // Αν δεν μπορεί να γίνει parse (π.χ. γράμματα ή άκυρο format), το θεωρούμε invalid
            return PhoneNumberValidationResult.invalid(rawPhoneNumber);
        }
    }

}
