package gr.hua.dit.petcare.web.ui;

import gr.hua.dit.petcare.core.dto.PetDto;

import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Pet;

import gr.hua.dit.petcare.core.service.PetService;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

import jakarta.validation.groups.Default;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller για τις λειτουργίες (CRUD) των κατοικίδιων.
 * Περιλαμβάνει: εμφάνιση των κατοικίδιων του χρήστη (GET /pets),
 * φόρμα για δημιουργία κατοικίδιου (GET /pets/new),
 * δημιουργία κατοικίδιου (POST /pets/new),
 * φόρμα επεξεργασίας κατοικίδιου (GET /pets/edit/{id}),
 * αποθήκευση αλλαγών (POST /pets/edit/{id}),
 * διαγραφή κατοικίδιου. (POST /pets/delete/{id})
 */
@Controller
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    /**
     * Εμφάνιση όλων των κατοικιδίων του χρήστη.
     * (GET /pets)
     */
    @GetMapping
    public String listPets(
            @AuthenticationPrincipal Person user,
            Model model
    ) {
        model.addAttribute("pets", petService.getPetsForOwner(user.getId()));
        model.addAttribute("fullName", user.getFullName());
        return "pets"; // pets.html
    }

    /**
     * Φόρμα για δημιουργία νέου κατοικιδίου.
     * (GET /pets/new)
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("pet", new PetDto());
        return "pet-form"; // φόρμα για δημιουργία και επεξεργασία δεδομένων κατοικίδιων
    }

    /**
     * Δημιουργία κατοικίδιου.
     * (POST /pets/new)
     */
    @PostMapping("/new")
    public String createPet(
            @AuthenticationPrincipal Person user,
            @Validated({PetDto.Create.class, Default.class}) @ModelAttribute("pet") PetDto dto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "pet-form";
        }

        petService.createPet(user.getId(), dto);
        return "redirect:/pets";
    }


    /**
     * Φόρμα για επεξεργασία κατοικιδίου.
     * (GET /pets/edit/{id})
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            @AuthenticationPrincipal Person user,
            Model model
    ) {
        Pet pet = petService.getPetById(id);

        if (pet == null || !pet.getOwner().getId().equals(user.getId())) {
            return "redirect:/pets"; // επιστρέφει ξανά στο pets
        }

        // μετατροπή του Entity σε DTO
        PetDto dto = new PetDto();
        dto.setName(pet.getName());
        dto.setSpecies(normalizeSpecies(pet.getSpecies()));

        dto.setBreed(pet.getBreed());
        dto.setAge(pet.getAge());

        model.addAttribute("pet", dto);
        model.addAttribute("petId", id);

        return "pet-form";
    }

    /**
     * Αποθήκευση αλλαγών κατοικιδίου.
     * (PATCH /pets/edit/{id})
     */
    @PostMapping("/edit/{id}")
    public String updatePet(
            @PathVariable Long id,
            @AuthenticationPrincipal Person user,
            @Valid @ModelAttribute("pet") PetDto dto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            Pet pet = petService.getPetById(id);
            if (pet == null || !pet.getOwner().getId().equals(user.getId())) {
                return "redirect:/pets";
            }

            dto.setName(pet.getName());
            dto.setSpecies(normalizeSpecies(pet.getSpecies()));
            dto.setBreed(pet.getBreed());

            model.addAttribute("petId", id);
            return "pet-form";
        }

        petService.updatePet(id, user.getId(), dto); // service ενημερώνει ΜΟΝΟ age
        return "redirect:/pets";
    }

    /**
     * Διαγραφή κατοικιδίου.
     * (POST /pets/delete/{id})
     */
    @PostMapping("/delete/{id}")
    public String deletePet(
            @PathVariable Long id,
            @AuthenticationPrincipal Person user,
            RedirectAttributes redirectAttributes
    ) {
        try {
            petService.deletePet(id, user.getId());
        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/pets";
    }

    /**
     * "Dog" --> "DOG".
     */
    private static String normalizeSpecies(String species) {
        if (species == null) return null;
        return species.trim().toUpperCase();
    }


}
