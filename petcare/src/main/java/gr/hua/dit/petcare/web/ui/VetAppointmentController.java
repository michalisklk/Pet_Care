package gr.hua.dit.petcare.web.ui;

import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Role;
import gr.hua.dit.petcare.core.repository.UserRepository;
import gr.hua.dit.petcare.core.service.AppointmentService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
public class VetAppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    public VetAppointmentController(AppointmentService appointmentService,
                                    UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
    }

    //Vet βλέπει ολα τα ραντεβου
    @GetMapping("/vet/appointments/all")
    public String all(@AuthenticationPrincipal Person vet, Model model) {

        if (vet == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }
        if (vet.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets");
        }

        model.addAttribute("vet", vet);
        model.addAttribute("appointments",
                appointmentService.getAppointmentsForVet(vet.getId()));

        return "vetallappointments";
    }

    //Vet βλέπει τα PENDING ραντεβου
    @GetMapping("/vet/appointments")
    public String pending(@AuthenticationPrincipal Person vet, Model model) {

        if (vet == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }
        if (vet.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets");
        }

        model.addAttribute("vet", vet);
        model.addAttribute("pendingAppointments",
                appointmentService.getPendingAppointmentsForVet(vet.getId()));

        return "vetpendappointments";
    }

    //Vet βλεπει τα CONFIRMED ραντεβου
    @GetMapping("/vet/appointments/confirmed")
    public String confirmed(@AuthenticationPrincipal Person vet, Model model) {

        if (vet == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }
        if (vet.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets");
        }

        model.addAttribute("vet", vet);
        model.addAttribute("confirmedAppointments",
                appointmentService.getConfirmedAppointmentsForVet(vet.getId()));

        return "vetconfirmedappointments";
    }

    //Vet βλεπει τα CANCELLED ραντεβου
    @GetMapping("/vet/appointments/cancelled")
    public String cancelled(@AuthenticationPrincipal Person vet, Model model) {

        if (vet == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }
        if (vet.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets");
        }

        model.addAttribute("vet", vet);
        model.addAttribute("cancelledAppointments",
                appointmentService.getCancelledAppointmentsForVet(vet.getId()));

        return "vetcancelledappointments";
    }

    //Vet βλεπει τα COMPLETED ραντεβου
    @GetMapping("/vet/appointments/completed")
    public String completed(@AuthenticationPrincipal Person vet, Model model) {

        if (vet == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }
        if (vet.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets");
        }

        model.addAttribute("vet", vet);
        model.addAttribute("completedAppointments",
                appointmentService.getCompletedAppointmentsForVet(vet.getId()));

        return "vetcompletedappointments";
    }

    //Confirm(το ραντεβου επιβεβαιωθηκε και θα γινει κανονικα)
    @PostMapping("/vet/appointments/{id}/confirm")
    public String confirm(@PathVariable("id") Long appointmentId,
                          @AuthenticationPrincipal Person vet) {

        if (vet == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }
        if (vet.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets");
        }

        appointmentService.confirm(appointmentId, vet);

        return "redirect:/vet/appointments"; // μένεις στη pending λίστα (το confirmed φεύγει από εδώ)
    }

    //Cancel(το ραντεβου ακυρωθηκε και δεν θα γινει)
    @PostMapping("/vet/appointments/{id}/cancel")
    public String cancel(@PathVariable("id") Long appointmentId,
                         @AuthenticationPrincipal Person vet,
                         @RequestParam(value = "notes", required = false) String notes,
                         @RequestParam(value = "from", required = false, defaultValue = "pending") String from) {

        if (vet == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }
        if (vet.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets");
        }

        appointmentService.cancelAsVet(appointmentId, vet, notes);

        if ("confirmed".equalsIgnoreCase(from)) {
            return "redirect:/vet/appointments/confirmed";
        }
        return "redirect:/vet/appointments";
    }

    //Complete(το ραντεβού ολοκληρώθηκε επιτυχώς)
    @PostMapping("/vet/appointments/{id}/complete")
    public String complete(@PathVariable("id") Long appointmentId,
                           @AuthenticationPrincipal Person vet,
                           @RequestParam(value = "notes", required = false) String notes) {

        if (vet == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }
        if (vet.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets");
        }

        appointmentService.complete(appointmentId, vet, notes);

        return "redirect:/vet/appointments/confirmed";
    }
}
