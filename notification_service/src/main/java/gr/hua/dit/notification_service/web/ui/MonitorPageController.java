package gr.hua.dit.notification_service.web.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Αυτός ο controller απλά σερβίρει τη σελίδα monitor (Thymeleaf template).
 */
@Controller
public class MonitorPageController {

    @GetMapping("/")
    public String home() {
        // επιστρέφει monitor.html από templates
        return "monitor";
    }
}
