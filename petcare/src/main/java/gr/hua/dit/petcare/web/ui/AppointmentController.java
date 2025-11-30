package gr.hua.dit.petcare.web.ui;

import gr.hua.dit.petcare.core.model.Appointment;
import gr.hua.dit.petcare.core.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
    // URL: http://localhost:8080/appointments?ownerId=1
    // GET appointments?ownerId=1  δείχνει τη φόρμα
    @GetMapping("/appointments")
    public String showAppointmentForm(@RequestParam Long ownerId, Model model) {

        model.addAttribute("ownerId", ownerId);

        return "appointments"; // αντιστοιχεί στο appointments.html
    }

    // POST appointments δημιουργεί ραντεβού για τον owner
    @PostMapping("/appointments")
    public String createAppointment(@RequestParam Long ownerId,
                                    @RequestParam Long petId,
                                    @RequestParam Long vetId,
                                    @RequestParam
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                    LocalDateTime start,
                                    @RequestParam
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                    LocalDateTime end,
                                    @RequestParam(required = false) String reason,
                                    Model model) {

        try {
            Appointment created = appointmentService.createAppointment(
                    ownerId, petId, vetId, start, end, reason
            );
            // Μήνυμα επιτυχίας
            model.addAttribute("successMessage", "Το ραντεβού δημιουργήθηκε με id = " + created.getId());
        } catch (Exception e) {
            // Μήνυμα λάθους
            model.addAttribute("errorMessage", e.getMessage());
        }

        model.addAttribute("ownerId", ownerId);
        return "appointments";
    }
}
