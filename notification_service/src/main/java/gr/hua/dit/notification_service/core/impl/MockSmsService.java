package gr.hua.dit.notification_service.core.impl;

import gr.hua.dit.notification_service.core.SmsService;
import gr.hua.dit.notification_service.core.model.SendSmsRequest;
import gr.hua.dit.notification_service.core.model.SendSmsResult;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Mock υλοποίηση του {@link SmsService}.
 * Δεν στέλνει πραγματικά SMS, απλά καταγράφει το αίτημα στο log
 * και επιστρέφει ότι το μήνυμα στάλθηκε επιτυχώς.
 */
@Service
public class MockSmsService implements SmsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockSmsService.class);

    @Override
    public SendSmsResult send(@Valid SendSmsRequest sendSmsRequest) {
        // Εμφανίζουμε στο log το "εικονικό" SMS που θα αποστελλόταν
        LOGGER.info("Mock SMS -> to: {}, content: {}",
                sendSmsRequest.e164(), sendSmsRequest.content());

        // Επειδή πρόκειται για mock υλοποίηση, θεωρούμε ότι το SMS στάλθηκε επιτυχώς
        return new SendSmsResult(true);
    }
}
