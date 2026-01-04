package gr.hua.dit.notification_service.web.rest;

import gr.hua.dit.notification_service.core.SmsService;
import gr.hua.dit.notification_service.core.model.SendSmsRequest;
import gr.hua.dit.notification_service.core.model.SendSmsResult;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoint της εξωτερικής υπηρεσίας
 * Η PetCare το καλεί σαν black box:
 * POST /api/v1/sms  { e164, content }
 */
@RestController
@RequestMapping(value = "/api/v1/sms", produces = MediaType.APPLICATION_JSON_VALUE)
public class SmsResource {

    private final SmsService smsService;

    // constructor injection: ο Spring δίνει το σωστό SmsService (Routee ή Mock)
    public SmsResource(SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SendSmsResult> sendSms(@RequestBody @Valid SendSmsRequest req) {
        SendSmsResult result = smsService.send(req);
        return ResponseEntity.ok(result);
    }
}
