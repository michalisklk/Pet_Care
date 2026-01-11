package gr.hua.dit.notification_service.core.impl;

import gr.hua.dit.notification_service.config.RouteeProperties;
import gr.hua.dit.notification_service.core.SmsService;
import gr.hua.dit.notification_service.core.model.SendSmsRequest;
import gr.hua.dit.notification_service.core.model.SendSmsResult;
import gr.hua.dit.notification_service.core.monitor.SmsEvent;
import gr.hua.dit.notification_service.core.monitor.SmsEventStore;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Service
public class RouteeSmsService implements SmsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteeSmsService.class);

    private static final String AUTHENTICATION_URL = "https://auth.routee.net/oauth/token";
    private static final String SMS_URL = "https://connect.routee.net/sms";

    private final RestTemplate restTemplate;
    private final RouteeProperties routeeProperties;
    private final SmsEventStore smsEventStore;

    public RouteeSmsService(final RestTemplate restTemplate,
                            final RouteeProperties routeeProperties,
                            final SmsEventStore smsEventStore) {
        if (restTemplate == null) throw new NullPointerException();
        if (routeeProperties == null) throw new NullPointerException();
        if (smsEventStore == null) throw new NullPointerException();

        this.restTemplate = restTemplate;
        this.routeeProperties = routeeProperties;
        this.smsEventStore = smsEventStore;
    }

    @SuppressWarnings("rawtypes")
    @Cacheable("routeeAccessToken")
    public String getAccessToken() {
        LOGGER.info("Requesting Routee Access Token");

        final String credentials = this.routeeProperties.getAppId() + ":" + this.routeeProperties.getAppSecret();
        final String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encoded);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", headers);

        final ResponseEntity<Map> response =
                this.restTemplate.exchange(AUTHENTICATION_URL, HttpMethod.POST, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Failed to obtain Routee access token. Status=" + response.getStatusCode());
        }

        final Object token = response.getBody().get("access_token");
        if (token == null) throw new IllegalStateException("Routee token response did not contain access_token");

        return token.toString();
    }

    @Override
    public SendSmsResult send(@Valid final SendSmsRequest sendSmsRequest) {
        if (sendSmsRequest == null) throw new NullPointerException();

        final String e164 = sendSmsRequest.e164();
        final String content = sendSmsRequest.content();

        boolean sent = false;

        try {
            final String accessToken = getAccessToken();

            final HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            final Map<String, Object> body = Map.of(
                    "body", content,
                    "to", e164,
                    "from", this.routeeProperties.getSender()
            );

            final HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            final ResponseEntity<String> response = this.restTemplate.postForEntity(SMS_URL, entity, String.class);

            LOGGER.info("Routee response: {}", response);

            sent = response.getStatusCode().is2xxSuccessful();
        } catch (Exception ex) {
            LOGGER.error("Routee SMS send failed for {}. Reason: {}", e164, ex.getMessage(), ex);
            sent = false;
        }

        // κρατάμε ιστορικό για UI (όπως και στο Mock)
        smsEventStore.add(new SmsEvent(
                LocalDateTime.now(),
                e164,
                content,
                sent
        ));

        return new SendSmsResult(sent);
    }
}
