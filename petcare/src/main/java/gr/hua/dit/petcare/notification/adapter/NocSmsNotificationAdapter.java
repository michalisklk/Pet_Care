package gr.hua.dit.petcare.notification.adapter;

import gr.hua.dit.petcare.notification.dto.SendSmsRequest;
import gr.hua.dit.petcare.notification.dto.SendSmsResult;
import gr.hua.dit.petcare.notification.port.SmsNotificationPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Adapter της εφαρμογής PetCare προς την εξωτερική υπηρεσία ειδοποιήσεων
 * Υλοποιεί το SmsNotificationPort και, εσωτερικά, καλεί το REST API
 * της εξωτερικής υπηρεσίας χρησιμοποιώντας RestTemplate.
 */
@Component
public class NocSmsNotificationAdapter implements SmsNotificationPort {

    private final RestTemplate restTemplate;
    private final String smsServiceUrl;

    /**
     * Το URL της εξωτερικής υπηρεσίας διαβάζεται από τα application properties.
     */
    public NocSmsNotificationAdapter(RestTemplate restTemplate,
                                     @Value("${external.sms-service.url}") String smsServiceUrl) {
        this.restTemplate = restTemplate;
        this.smsServiceUrl = smsServiceUrl;
    }

    @Override
    public boolean sendSms(String phoneE164, String content) {
        // Δημιουργούμε το request object που θα σταλεί ως JSON στο εξωτερικό API
        SendSmsRequest request = new SendSmsRequest(phoneE164, content);

        try {
            // Κλήση στην εξωτερική υπηρεσία με HTTP POST
            SendSmsResult result =
                    restTemplate.postForObject(smsServiceUrl, request, SendSmsResult.class);

            // Αν για κάποιο λόγο η απάντηση είναι null, θεωρούμε ότι η αποστολή απέτυχε
            if (result == null) {
                return false;
            }

            // Χρησιμοποιούμε το πεδίο sent της απάντησης για να επιστρέψουμε το αποτέλεσμα
            return result.sent();
        } catch (Exception e) {
            // Αν η υπηρεσία δεν είναι διαθέσιμη ή γίνει κάποιο άλλο σφάλμα, επιστρέφουμε false
            return false;
        }
    }
}
