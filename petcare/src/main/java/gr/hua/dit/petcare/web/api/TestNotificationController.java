package gr.hua.dit.petcare.web.api;

import gr.hua.dit.petcare.notification.port.SmsNotificationPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Απλό test controller για να δοκιμάζουμε την αποστολή SMS
 * μέσω της εξωτερικής υπηρεσίας από την εφαρμογή PetCare.
 */
@RestController
@RequestMapping("/api/test-notifications")
public class TestNotificationController {

    // Χρησιμοποιούμε το port, όχι απευθείας τον adapter
    private final SmsNotificationPort smsNotificationPort;

    public TestNotificationController(SmsNotificationPort smsNotificationPort) {
        this.smsNotificationPort = smsNotificationPort;
    }

    /**
     * Endpoint: POST /api/test-notifications/sms
     *
     * Παίρνει phone και content σαν request parameters
     * και στέλνει SMS μέσω της εξωτερικής υπηρεσίας.
     */
    @PostMapping("/sms")
    public ResponseEntity<String> testSms(
            @RequestParam String phone,
            @RequestParam String content) {

        boolean sent = smsNotificationPort.sendSms(phone, content);

        // Πολύ απλή απάντηση για debug
        return ResponseEntity.ok("sent=" + sent);
    }
}
