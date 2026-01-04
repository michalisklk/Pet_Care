package gr.hua.dit.notification_service.web.ui.model;

import java.time.LocalDateTime;

public record SmsEvent(
        LocalDateTime at,
        String toE164,
        String content,
        boolean sent
) {}
