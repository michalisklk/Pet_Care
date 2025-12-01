package gr.hua.dit.petcare.web.ui;

import gr.hua.dit.petcare.core.dto.AppointmentDto;
import gr.hua.dit.petcare.core.model.Appointment;
import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Pet;
import gr.hua.dit.petcare.core.model.Role;
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

    //GET
    // π.χ. http://localhost:8080/appointments?ownerId=1
    @GetMapping("/appointments")
    public String showAppointmentForm(@RequestParam("ownerId") Long ownerId,
                                      Model model) {
        //Φέρνουμε από τη βάση τον owner με το δοσμένο ownerId.
        Person owner = userRepository.findById(ownerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));
        //Φέρνουμε όλα τα κατοικίδια αυτού του ιδιοκτήτη
        List<Pet> pets = petRepository.findByOwner(owner);
        //Φέρνουμε όλους τους person με ρολό vet
        List<Person> vets = userRepository.findByRole(Role.VET);

        //Δημιουργούμε ένα κενό DTO για τη φόρμα και βάζουμε μέσα το ownerId
        AppointmentDto dto = new AppointmentDto();
        dto.setOwnerId(ownerId);

        model.addAttribute("owner", owner);
        model.addAttribute("pets", pets);
        model.addAttribute("vets", vets);
        model.addAttribute("appointment", dto);

        return "appointments";
    }

    //POST
    @PostMapping("/appointments")
    public String submitAppointment(@Valid @ModelAttribute("appointment") AppointmentDto appointmentDto,
                                    BindingResult bindingResult,
                                    Model model) {

        // Ξαναφορτώνουμε τον owner & τα pets για το view είτε Έχει λάθη είτε οχι δεν είμαι σίγουρος οτι λειτουργεί 100% σωστά ακόμα
        Person owner = userRepository.findById(appointmentDto.getOwnerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));
        List<Pet> pets = petRepository.findByOwner(owner);
        List<Person> vets=userRepository.findByRole(Role.VET);
        model.addAttribute("owner", owner);
        model.addAttribute("pets", pets);
        model.addAttribute("vets", vets);
        model.addAttribute("appointment", appointmentDto);

        //Αν βρήκε λάθος γυρνάει πίσω στο appointments
        if (bindingResult.hasErrors()) {
            return "appointments";
        }

        try {
            // Καλείτε το service που κάνει τα εξης:
            //έλεγχο οτι υπάρχει ο owner τo pet και ο vet/οτι το pet ανηκει στον owner/overlap (ενα ραντεβού πάνω στο άλλο αν και αυτό δεν το εχω τσεκάρει ακόμα)
            Appointment created = appointmentService.createAppointment(
                    appointmentDto.getOwnerId(),
                    appointmentDto.getPetId(),
                    appointmentDto.getVetId(),
                    appointmentDto.getStart(),
                    appointmentDto.getEnd(),
                    appointmentDto.getReason()
            );
            //Αν ολα πίγαν καλά εμφανίζεται μυνημα επιτυχίας με το ID του ραντεβού
            model.addAttribute("successMessage",
                    "Το ραντεβού δημιουργήθηκε με ID: " + created.getId());

        } catch (EntityNotFoundException | IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ένα απρόσμενο σφάλμα συνέβη.");
        }
        //Γυρνάμε πίσω στο appointments.html
        return "appointments";
    }
}
