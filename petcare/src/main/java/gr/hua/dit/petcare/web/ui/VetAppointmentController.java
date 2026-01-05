package gr.hua.dit.petcare.web.ui;

import gr.hua.dit.petcare.core.model.Appointment;
import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Role;
import gr.hua.dit.petcare.core.repository.UserRepository;
import gr.hua.dit.petcare.core.service.AppointmentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import java.util.List;

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
    public String getAppointmentsForVet(@AuthenticationPrincipal Person vet, Model model) {

        if (vet == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");

        List<Appointment> all = appointmentService.getAppointmentsForVet(vet.getId());
        model.addAttribute("vet", vet);
        model.addAttribute("appointments", all);

        return "vetallappointments";
    }


    //Vet βλέπει τα PENDING ραντεβου
    @GetMapping("/vet/appointments")
    public String getPendingAppointmentsForVet(@AuthenticationPrincipal Person vet, Model model) {

        if (vet == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");

        List<Appointment> pending = appointmentService.getPendingAppointmentsForVet(vet.getId());
        model.addAttribute("vet", vet);
        model.addAttribute("pendingAppointments", pending);

        return "vetpendappointments";
    }


    //Confirm(το ραντεβου επιβεβαιωθηκε και θα γινει κανονικα)
    @PostMapping("/vet/appointments/{id}/confirm")
    public String confirm(@PathVariable("id") Long appointmentId,
                          @AuthenticationPrincipal Person vet) {

        if (vet == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");

        try {
            appointmentService.confirm(appointmentId, vet);
        } catch (EntityNotFoundException | IllegalStateException e) {}

        return "redirect:/vet/appointments";
    }


    //Cancel(το ραντεβου ακυρωθηκε και δεν θα γινει)
    @PostMapping("/vet/appointments/{id}/cancel")
    public String cancel(@PathVariable("id") Long appointmentId,
                         @AuthenticationPrincipal Person vet,
                         @RequestParam(value = "notes", required = false) String notes) {

        if (vet == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");

        try {
            appointmentService.cancelAsVet(appointmentId, vet, notes);
        } catch (EntityNotFoundException | IllegalStateException e) {}

        return "redirect:/vet/appointments";
    }


    //Complete(το ραντεβου ολοκληρώθηκε επιτυχώς)
    @PostMapping("/vet/appointments/{id}/complete")
    public String complete(@PathVariable("id") Long appointmentId,
                           @AuthenticationPrincipal Person vet,
                           @RequestParam(value = "notes", required = false) String notes) {

        if (vet == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");

        try {
            appointmentService.complete(appointmentId, vet, notes);
        } catch (EntityNotFoundException | IllegalStateException e) {}

        return "redirect:/vet/appointments";
    }

}

