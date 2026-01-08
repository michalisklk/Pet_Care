package gr.hua.dit.petcare.web.ui;

import gr.hua.dit.petcare.core.dto.AppointmentDto;
import gr.hua.dit.petcare.core.model.*;
import gr.hua.dit.petcare.core.service.AppointmentService;
import gr.hua.dit.petcare.core.service.PetService;
import gr.hua.dit.petcare.core.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;
    private final PetService petService;

    public AppointmentController(AppointmentService appointmentService,
                                 UserService userService,
                                 PetService petService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.petService = petService;
    }

    @GetMapping("/appointments")
    public String showAppointmentForm(@AuthenticationPrincipal Person owner, Model model) {

        if (owner == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        List<Pet> pets = petService.getPetsForOwner(owner.getId());
        List<Person> vets = userService.getUsersByRole(Role.VET);

        model.addAttribute("owner", owner);
        model.addAttribute("pets", pets);
        model.addAttribute("vets", vets);
        model.addAttribute("appointment", new AppointmentDto());
        model.addAttribute("reasons", AppointmentReason.values());

        return "appointments";
    }

    @PostMapping("/appointments")
    public String submitAppointment(@AuthenticationPrincipal Person owner,
                                    @Valid @ModelAttribute("appointment") AppointmentDto appointmentDto,
                                    BindingResult bindingResult,
                                    Model model) {

        if (owner == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        // Reload για να ξαναγεμίσουν τα dropdowns αν έχει λάθος
        List<Pet> pets = petService.getPetsForOwner(owner.getId());
        List<Person> vets = userService.getUsersByRole(Role.VET);

        model.addAttribute("owner", owner); // owner από session
        model.addAttribute("pets", pets);
        model.addAttribute("vets", vets);
        model.addAttribute("appointment", appointmentDto);
        model.addAttribute("reasons", AppointmentReason.values());

        //Αν βρήκε λάθος γυρνάει πίσω στο appointments
        if (bindingResult.hasErrors()) {
            return "appointments";
        }

        try {
            Appointment created = appointmentService.createAppointment(
                    owner.getId(),
                    appointmentDto.getPetId(),
                    appointmentDto.getVetId(),
                    appointmentDto.getStart(),
                    appointmentDto.getReason()
            );

            model.addAttribute("successMessage",
                    "Το ραντεβού δημιουργήθηκε με ID: " + created.getId());

        } catch (EntityNotFoundException | IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ένα απρόσμενο σφάλμα συνέβη.");
        }

        return "appointments";
    }

    //Ο owner μπορεί να βλέπει τα ραντεβού του
    @GetMapping("/appointments/owners")
    public String ownerAppointments(@AuthenticationPrincipal Person user, Model model){
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }
        if(user.getRole()!=Role.PET_OWNER){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only pets can cancel");
        }

        model.addAttribute("owner", user);
        model.addAttribute("appointments", appointmentService.getAppointmentsForOwner(user.getId()));
        return "ownerappointments";
    }
}
