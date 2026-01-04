package gr.hua.dit.notification_service.web.ui;

import gr.hua.dit.notification_service.web.ui.model.SmsEvent;
import gr.hua.dit.notification_service.web.ui.store.SmsEventStore;
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
        return store.latest(limit);
    }

    /**
     * Καθαρίζει τα events (κουμπί Clear στο UI).
     */
    @PostMapping("/sms-events/clear")
    public void clear() {
        store.clear();
    }
}
