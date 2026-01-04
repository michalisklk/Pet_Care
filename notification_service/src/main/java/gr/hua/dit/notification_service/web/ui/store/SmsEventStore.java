package gr.hua.dit.notification_service.web.ui.store;

import gr.hua.dit.notification_service.web.ui.model.SmsEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Κρατάμε τα SMS events στη μνήμη για το UI monitor.
 */
@Component
public class SmsEventStore {

    // Deque: βάζουμε τα καινούρια μπροστά, πετάμε τα παλιά από πίσω
    private final Deque<SmsEvent> events = new ConcurrentLinkedDeque<>();

    // Πόσα events κρατάμε το πολύ
    private static final int MAX_SIZE = 50;

    /**
     * Αποθηκεύει ένα νέο event ως "τελευταίο".
     */
    public void add(SmsEvent e) {
        events.addFirst(e);

        // Κρατάμε μόνο τα τελευταία MAX_SIZE events
        while (events.size() > MAX_SIZE) {
            events.removeLast();
        }
    }

    /**
     * Επιστρέφει τα πιο πρόσφατα events μέχρι limit.
     */
    public List<SmsEvent> latest(int limit) {
        int safeLimit = Math.max(0, limit);

        List<SmsEvent> out = new ArrayList<>(Math.min(safeLimit, events.size()));

        Iterator<SmsEvent> it = events.iterator(); // ξεκινά από τα πιο πρόσφατα (front)
        int count = 0;

        while (it.hasNext() && count < safeLimit) {
            out.add(it.next());
            count++;
        }

        return out;
    }

    /**
     * Καθαρίζει όλα τα events (κουμπί Clear στο UI).
     */
    public void clear() {
        events.clear();
    }
}
