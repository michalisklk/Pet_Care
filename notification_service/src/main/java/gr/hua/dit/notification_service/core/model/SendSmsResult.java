package gr.hua.dit.notification_service.core.model;

/**
 * Αποτέλεσμα αποστολής SMS (DTO).
 *
 * Χρησιμοποιείται ως:
 * - τιμή επιστροφής της μεθόδου SmsService.send(...)
 * - σώμα της HTTP απόκρισης από το endpoint /api/v1/sms
 *
 * Το μόνο πεδίο που περιέχει είναι:
 * - sent : true αν θεωρούμε ότι το SMS στάλθηκε επιτυχώς, false διαφορετικά
 */
public record SendSmsResult(boolean success) {
}
