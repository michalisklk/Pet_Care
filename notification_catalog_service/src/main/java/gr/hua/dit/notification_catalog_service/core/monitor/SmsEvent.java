package gr.hua.dit.notification_catalog_service.core.monitor;

import java.time.LocalDateTime;

public record SmsEvent(
        LocalDateTime at,
        String toE164,
        String content,
        boolean sent
) {}
