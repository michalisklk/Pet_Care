package gr.hua.dit.notification_service.config;

import gr.hua.dit.notification_service.core.SmsService;
import gr.hua.dit.notification_service.core.impl.MockSmsService;
import gr.hua.dit.notification_service.core.impl.RouteeSmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

/**
 * Επιλέγει ποιο SmsService θα χρησιμοποιεί η εφαρμογή.
 * - Αν έχουμε routee credentials -> RouteeSmsService (πραγματικό SMS)
 * - Αλλιώς MockSmsService (για δοκιμές)
 */
@Configuration
public class SmsServiceSelector {

    private static final Logger log = LoggerFactory.getLogger(SmsServiceSelector.class);

    @Bean
    @Primary
    public SmsService smsService(RouteeProperties routeeProperties,
                                 RouteeSmsService routeeSmsService,
                                 MockSmsService mockSmsService) {

        // Αν υπάρχουν credentials, πάμε σε real provider
        boolean hasCreds = StringUtils.hasText(routeeProperties.getAppId())
                && StringUtils.hasText(routeeProperties.getAppSecret())
                && StringUtils.hasText(routeeProperties.getSender());


        if (hasCreds) {
            log.info("Using RouteeSmsService (real SMS)");
            return routeeSmsService;
        }

        // Αλλιώς, χρησιμοποιούμε mock
        log.info("Using MockSmsService (no Routee credentials)");
        return mockSmsService;
    }
}
