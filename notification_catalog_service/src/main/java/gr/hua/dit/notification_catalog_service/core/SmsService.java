package gr.hua.dit.notification_catalog_service.core;

import gr.hua.dit.notification_catalog_service.core.model.SendSmsRequest;
import gr.hua.dit.notification_catalog_service.core.model.SendSmsResult;

/**
 * interface για υπηρεσία αποστολής SMS.
 * Ορίζει το συμβόλαιο που πρέπει να υλοποιεί κάθε κλάση που στέλνει SMS:
 * δέχεται ένα {@link SendSmsRequest} με τα στοιχεία του μηνύματος
 * και επιστρέφει ένα {@link SendSmsResult} με το αποτέλεσμα της αποστολής.
 */
public interface SmsService {

    /**
     * @param sendSmsRequest τα στοιχεία του SMS (αριθμός και κείμενο)
     * @return αντικείμενο {@link SendSmsResult} που δείχνει αν θεωρούμε
     *         ότι η αποστολή ήταν επιτυχής ή όχι
     */
    SendSmsResult send(SendSmsRequest sendSmsRequest);
}
