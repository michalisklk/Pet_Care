package gr.hua.dit.petcare.web.ui;

import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Pet;
import gr.hua.dit.petcare.core.model.Role;
import gr.hua.dit.petcare.core.service.PetService;
import gr.hua.dit.petcare.core.service.VaccineSuggestionService;
import gr.hua.dit.petcare.notification.dto.VaccineInfo;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Controller
@RequestMapping("/vaccines")
public class VaccinesUiController {

    private final VaccineSuggestionService vaccineSuggestionService;
    private final PetService petService;

    public VaccinesUiController(VaccineSuggestionService vaccineSuggestionService, PetService petService) {
        this.vaccineSuggestionService = vaccineSuggestionService;
        this.petService = petService;
    }

    /**
     * UI Page:
     * /vaccines/recommendations?petId=...
     * ή /vaccines/recommendations?species=DOG&age=2
     */
    @GetMapping("/recommendations")
    public String recommendations(
            @AuthenticationPrincipal Person user,
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) Integer age,
            Model model
    ) {
        // εδώ κάνουμε βασικούς ελέγχους ασφάλειας
        requireOwner(user);

        final String finalSpecies;
        final int finalAge;
        final String pageTitle;
        final String pageSubtitle;
        final List<VaccineInfo> vaccines;

        // ---------------------------
        // Case 1: έχω συγκεκριμένο petId
        // ---------------------------
        if (petId != null) {
            Pet pet = petService.getPetById(petId);

            // άμα δεν υπάρχει pet ή είναι inactive, βγάζουμε 404
            if (pet == null || !pet.isActive()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found");
            }

            // δεν αφήνουμε άλλον χρήστη να δει pet που δεν του ανήκει
            if (pet.getOwner() == null || !Objects.equals(pet.getOwner().getId(), user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This pet does not belong to you");
            }

            finalSpecies = normalizeSpecies(pet.getSpecies());
            finalAge = Math.max(0, pet.getAge()); // δεν θέλουμε αρνητικές ηλικίες
            pageTitle = "Vaccine suggestions";
            pageSubtitle = "Pet: " + pet.getName() + " • " + finalSpecies + " • Age: " + finalAge + " years";

            // εδώ πάμε service -> port(adapter) -> external notification_service (vaccines endpoint)
            vaccines = vaccineSuggestionService.typicalVaccinesForPet(petId, user.getId());

            // περνάμε και το pet στο template αν θέλουμε να το δείξουμε
            model.addAttribute("pet", pet);

        } else {
            // ---------------------------
            // Case 2: Preview mode (species + age)
            // ---------------------------
            // αν δεν έχω petId, τότε πρέπει να έχω species και age, αλλιώς είναι λάθος request
            if (species == null || species.trim().isEmpty() || age == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing params. Use either petId, or species+age.");
            }

            finalSpecies = normalizeSpecies(species);
            finalAge = Math.max(0, age);
            pageTitle = "Vaccine suggestions (preview)";
            pageSubtitle = "Species: " + finalSpecies + " • Age: " + finalAge + " years";

            // εδώ δεν έχουμε pet, άρα παίρνουμε τυπικά εμβόλια μόνο από είδος
            vaccines = vaccineSuggestionService.typicalVaccinesForSpecies(finalSpecies);
        }

        // φτιάχνουμε ένα μήνυμα ανά ηλικία, και ανά εμβόλιο (optional)
        AgeBasedPlan plan = buildAgeBasedPlan(finalSpecies, finalAge, vaccines);

        // ---------------------------
        // Model attributes για το thymeleaf template
        // ---------------------------
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("pageSubtitle", pageSubtitle);
        model.addAttribute("species", finalSpecies);
        model.addAttribute("age", finalAge);
        model.addAttribute("vaccines", vaccines);

        model.addAttribute("planSummary", plan.summary());
        model.addAttribute("planSummaryType", plan.summaryType());
        model.addAttribute("perVaccineAdvice", plan.perVaccineAdvice());
        model.addAttribute("defaultRowAdvice", plan.defaultRowAdvice());

        // αν external service πέσει, adapter κάνει fallback σε empty list
        // οπότε εδώ δείχνουμε “empty message” για να μην φαίνεται σπασμένο το UI
        if (vaccines == null || vaccines.isEmpty()) {
            model.addAttribute(
                    "emptyMessage",
                    "No vaccine list found for species: " + finalSpecies + " (mock).\n" +
                            "Try another species or ask your veterinarian."
            );
        }

        // disclaimer
        model.addAttribute(
                "disclaimer",
                "Demo only: this page shows indicative (mock) suggestions and is not medical advice. Always ask your veterinarian."
        );

        return "vaccines-recommendations";
    }

    /**
     * common security checks για το UI page
     */
    private static void requireOwner(Person user) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        if (user.getRole() != Role.PET_OWNER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owners can view this page");
        }
    }

    /**
     * normalization ώστε το DOG, dog, Dog να είναι το ίδιο.
     * (Γιατί στο external service τα keys είναι DOG/CAT/...)
     */
    private static String normalizeSpecies(String species) {
        return species == null ? null : species.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * πακετάκι που στέλνουμε στο template:
     * - summary + τύπος (success/error)
     * - advice ανά vaccine code
     * - default advice για όσα δεν έχουν ειδικό advice
     */
    private record AgeBasedPlan(
            String summary,
            String summaryType, // "success" or "error"
            Map<String, String> perVaccineAdvice,
            String defaultRowAdvice
    ) { }

    /**
     * απλό age-based κανόνας
     * Δεν αλλάζουμε λειτουργικότητα — απλά φτιάχνουμε messages που θα δείξει το UI.
     */
    private static AgeBasedPlan buildAgeBasedPlan(String species, int ageYears, List<VaccineInfo> vaccines) {
        int age = Math.max(0, ageYears);
        String s = species == null ? "" : species.trim().toUpperCase(Locale.ROOT);

        // LinkedHashMap για να κρατάει stable σειρά όταν το δείχνει στο UI
        Map<String, String> map = new LinkedHashMap<>();

        // αν δεν έχω vaccines, βγάζω error-type summary
        if (vaccines == null || vaccines.isEmpty()) {
            return new AgeBasedPlan(
                    "No vaccine list was returned for this species (mock). Please ask your veterinarian.",
                    "error",
                    map,
                    "Ask your veterinarian."
            );
        }

        // απλά rules για DOG/CAT/HAMSTER, αλλιώς generic message
        switch (s) {
            case "DOG" -> {
                if (age <= 1) {
                    map.put("DHPP", "Now: start puppy core series (vet schedule).");
                    map.put("RABIES", "Ask your vet (local rules).");
                    map.put("BORDETELLA", "Only if high exposure (daycare/boarding).");
                    return new AgeBasedPlan(
                            "Dog (≤ 1 year): start the core puppy vaccines. Rabies timing depends on local rules and your vet.",
                            "success",
                            map,
                            "Discuss the puppy schedule with your vet."
                    );
                }
                if (age <= 6) {
                    map.put("DHPP", "Check if a booster is due (ask your vet).");
                    map.put("RABIES", "Check the booster/legal requirement (ask your vet).");
                    map.put("BORDETELLA", "Only if high exposure to other dogs.");
                    return new AgeBasedPlan(
                            "Dog (2–6 years): check if any boosters are due.",
                            "success",
                            map,
                            "Check boosters with your vet."
                    );
                }
                map.put("DHPP", "Ask your vet; boosters depend on health and risk.");
                map.put("RABIES", "Ask your vet + check local requirement.");
                map.put("BORDETELLA", "Usually optional; decide by exposure + vet.");
                return new AgeBasedPlan(
                        "Dog (7+ years): talk to your vet first; boosters are tailored to health and risk.",
                        "success",
                        map,
                        "Talk to your vet first (senior pet)."
                );
            }

            case "CAT" -> {
                if (age <= 1) {
                    map.put("FVRCP", "Now: start kitten core series (vet schedule).");
                    map.put("RABIES", "Ask your vet (local rules).");
                    map.put("FELV", "Often depends on lifestyle (outdoor/exposure).");
                    return new AgeBasedPlan(
                            "Cat (≤ 1 year): start the core kitten vaccines. FeLV often depends on lifestyle.",
                            "success",
                            map,
                            "Discuss the kitten schedule with your vet."
                    );
                }
                if (age <= 6) {
                    map.put("FVRCP", "Check if a booster is due (ask your vet).");
                    map.put("RABIES", "Check the booster/legal requirement (ask your vet).");
                    map.put("FELV", "Depends on lifestyle (outdoor/exposure).");
                    return new AgeBasedPlan(
                            "Cat (2–6 years): check if any boosters are due; consider indoor vs outdoor.",
                            "success",
                            map,
                            "Check boosters with your vet."
                    );
                }
                map.put("FVRCP", "Ask your vet; boosters depend on health and risk.");
                map.put("RABIES", "Ask your vet + check local requirement.");
                map.put("FELV", "Depends on lifestyle; decide with your vet.");
                return new AgeBasedPlan(
                        "Cat (7+ years): talk to your vet first; boosters are tailored to health and risk.",
                        "success",
                        map,
                        "Talk to your vet first (senior pet)."
                );
            }

            case "HAMSTER" -> {
                String summary = (age <= 1)
                        ? "Hamster (young): routine vaccines are uncommon. Focus on good care and contact a vet if you notice symptoms."
                        : (age == 2)
                        ? "Hamster (adult): routine vaccines are uncommon. Focus on monitoring and good hygiene."
                        : "Hamster (senior): routine vaccines are uncommon. If something looks wrong, contact a vet early.";

                return new AgeBasedPlan(summary, "success", map, "Ask a vet experienced with small mammals.");
            }

            default -> {
                return new AgeBasedPlan(
                        "This demo does not have a simple age rule for this species. Please ask your veterinarian.",
                        "success",
                        map,
                        "Ask your veterinarian."
                );
            }
        }
    }
}
