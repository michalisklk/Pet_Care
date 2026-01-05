package gr.hua.dit.petcare.web.ui;


import gr.hua.dit.petcare.core.model.Person;

import gr.hua.dit.petcare.core.repository.UserRepository;
import gr.hua.dit.petcare.core.service.AppointmentService;

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
    public String all(@AuthenticationPrincipal Person vet, Model model) {

        if (vet == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
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

        model.addAttribute("vet", vet);
        model.addAttribute("pendingAppointments",
                appointmentService.getPendingAppointmentsForVet(vet.getId()));

        return "vetpendappointments";
    }



    //Confirm(το ραντεβου επιβεβαιωθηκε και θα γινει κανονικα)
    @PostMapping("/vet/appointments/{id}/confirm")
    public String confirm(@PathVariable("id") Long appointmentId,
                          @AuthenticationPrincipal Person vet) {

        if (vet == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        appointmentService.confirm(appointmentId, vet);

        return "redirect:/vet/appointments";
    }



    //Cancel(το ραντεβου ακυρωθηκε και δεν θα γινει)
    @PostMapping("/vet/appointments/{id}/cancel")
    public String cancel(@PathVariable("id") Long appointmentId,
                         @AuthenticationPrincipal Person vet,
                         @RequestParam(value = "notes", required = false) String notes) {

        if (vet == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        appointmentService.cancelAsVet(appointmentId, vet, notes);

        return "redirect:/vet/appointments";
    }



    //Complete(το ραντεβου ολοκληρώθηκε επιτυχώς)
    @PostMapping("/vet/appointments/{id}/complete")
    public String complete(@PathVariable("id") Long appointmentId,
                           @AuthenticationPrincipal Person vet,
                           @RequestParam(value = "notes", required = false) String notes) {

        if (vet == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        appointmentService.complete(appointmentId, vet, notes);

        return "redirect:/vet/appointments";
    }


}

