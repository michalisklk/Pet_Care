package gr.hua.dit.notification_service.web.ui;

import gr.hua.dit.notification_service.core.monitor.SmsEvent;
import gr.hua.dit.notification_service.core.monitor.SmsEventStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints για το monitor UI.
 * Το UI καλεί αυτά τα endpoints για να πάρει/καθαρίσει τα SMS events.
 */
@RestController
@RequestMapping("/api/v1/monitor")
public class MonitorResource {

    private final SmsEventStore store;

    public MonitorResource(SmsEventStore store) {
        this.store = store;
    }

    /**
     * Επιστρέφει τα πιο πρόσφατα SMS events σε JSON.
     * π.χ. GET http://localhost:8081/api/v1/monitor/sms-events?limit=50
     */
    @GetMapping("/sms-events")
    public List<SmsEvent> latest(@RequestParam(defaultValue = "50") int limit) {
        int capped = Math.min(50, Math.max(0, limit));
        return store.latest(capped);
    }


    /**
     * Καθαρίζει τα events (κουμπί Clear στο UI).
     */
    @PostMapping("/sms-events/clear")
    public void clear() {
        store.clear();
    }
}
