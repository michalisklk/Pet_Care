package gr.hua.dit.petcare.web.ui;

import gr.hua.dit.petcare.core.dto.AppointmentDto;
import gr.hua.dit.petcare.core.model.*;
import gr.hua.dit.petcare.core.repository.PetRepository;
import gr.hua.dit.petcare.core.repository.UserRepository;
import gr.hua.dit.petcare.core.service.AppointmentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import java.util.List;

@Controller
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;
    private final PetRepository petRepository;

    public AppointmentController(AppointmentService appointmentService,
                                 UserRepository userRepository,
                                 PetRepository petRepository) {
        if (appointmentService == null || userRepository == null || petRepository == null) {
            throw new NullPointerException("Dependencies are null");
        }
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
        this.petRepository = petRepository;
    }

    @GetMapping("/appointments")
    public String showAppointmentForm(@AuthenticationPrincipal Person owner, Model model) {

        if (owner == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        List<Pet> pets = petRepository.findByOwnerId(owner.getId());
        List<Person> vets = userRepository.findByRole(Role.VET);

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
        List<Pet> pets = petRepository.findByOwnerId(owner.getId());
        List<Person> vets = userRepository.findByRole(Role.VET);

        model.addAttribute("owner", owner);
        model.addAttribute("pets", pets);
        model.addAttribute("vets", vets);
        model.addAttribute("appointment", appointmentDto);
        model.addAttribute("reasons", AppointmentReason.values());

        //Αν βρήκε λάθος γυρνάει πίσω στο appointments
        if (bindingResult.hasErrors()) {
            return "appointments";
        }

        try {
            // Καλείτε το service που κάνει τα εξης:
            //έλεγχο οτι υπάρχει ο owner τo pet και ο vet/οτι το pet ανηκει στον owner/overlap
            //(ενα ραντεβού πάνω στο άλλο αν και αυτό δεν το εχω τσεκάρει ακόμα)
            Appointment created = appointmentService.createAppointment(
                    owner.getId(),                 // owner από session
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
}
