package gr.hua.dit.notification_service.core.impl;

import gr.hua.dit.notification_service.core.SmsService;
import gr.hua.dit.notification_service.core.model.SendSmsRequest;
import gr.hua.dit.notification_service.core.model.SendSmsResult;
import gr.hua.dit.notification_service.web.ui.model.SmsEvent;
import gr.hua.dit.notification_service.web.ui.store.SmsEventStore;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Mock υλοποίηση του SmsService.
 * Δεν στέλνει πραγματικά SMS
 */
@Service
public class MockSmsService implements SmsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockSmsService.class);

    private final SmsEventStore smsEventStore;

    public MockSmsService(SmsEventStore smsEventStore) {
        this.smsEventStore = smsEventStore;
    }

    @Override
    public SendSmsResult send(@Valid SendSmsRequest sendSmsRequest) {

        LOGGER.info("Στέλνεται SMS στον αριθμό: {}, με περιεχόμενο: {}",
                sendSmsRequest.e164(), sendSmsRequest.content());

        boolean sent = true;

        //κρατάμε ιστορικό για UI
//        smsEventStore.add(new SmsEvent(
//                LocalDateTime.now(),
//                sendSmsRequest.e164(),
//                sendSmsRequest.content(),
//                sent
//        ));

        return new SendSmsResult(sent);
    }
}
