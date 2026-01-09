package gr.hua.dit.petcare.web.api;

import gr.hua.dit.petcare.core.dto.PetDto;
import gr.hua.dit.petcare.core.model.Pet;
import gr.hua.dit.petcare.core.service.PetService;
import gr.hua.dit.petcare.web.api.dto.PetResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import gr.hua.dit.petcare.core.model.Person;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.ValidationException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pets")
public class PetApiController {

    private final PetService petService;

    public PetApiController(PetService petService) {
        this.petService = petService;
    }

    /**
     * GET /api/v1/pets?ownerId=1
     * Επιστρέφει όλα τα pets ενός owner (JSON).
     */
    @GetMapping
    public List<PetResponse> list(@AuthenticationPrincipal Person user) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        return petService.getPetsForOwner(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }


    /**
     * POST /api/v1/pets
     * Body: { "ownerId":1, "name":"Milo", "species":"Dog", "breed":"Husky", "age":3 }
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetResponse create(@AuthenticationPrincipal Person user,
                              @Valid @RequestBody CreatePetRequest req) {

        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");

        PetDto dto = new PetDto();
        dto.setName(req.name());
        dto.setSpecies(req.species());
        dto.setBreed(req.breed());
        dto.setAge(req.age());

        Pet saved = petService.createPet(user.getId(), dto);
        return toResponse(saved);
    }


    private PetResponse toResponse(Pet p) {
        return new PetResponse(
                p.getId(),
                p.getName(),
                p.getSpecies(),
                p.getBreed(),
                p.getAge(),
                p.getOwner() != null ? p.getOwner().getId() : null
        );
    }

    /**
     * DELETE /api/v1/pets/{id}
     * Soft-delete pet: active=false και ακύρωση PENDING/CONFIRMED ραντεβού (σύμφωνα με PetServiceImpl)
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Person user, @PathVariable Long id) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");

        try {
            petService.deletePet(id, user.getId());
        } catch (ValidationException ex) {
            String msg = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
            if (msg.contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
            }
            if (msg.contains("unauthorized")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    /**
     * PATCH /api/v1/pets/{id}
     * Μερική ενημέρωση πεδίων. Όσα δεν δοθούν μένουν όπως είναι.
     */
    @PatchMapping("/{id}")
    public PetResponse patch(@AuthenticationPrincipal Person user,
                             @PathVariable Long id,
                             @Valid @RequestBody UpdatePetRequest req) {

        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");

        Pet existing = petService.getPetById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found");
        }

        // Έλεγχος ιδιοκτησίας ώστε να επιστρέφεται καθαρά 403 (και όχι generic 400)
        if (existing.getOwner() == null || !existing.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized operation: This pet does not belong to this owner.");
        }

        // Δημιουργούμε πλήρες PetDto γιατί το PetDto έχει @NotBlank/@Min(1) και δεν επιτρέπει nulls
        PetDto dto = new PetDto();
        dto.setName(req.name() != null ? req.name() : existing.getName());
        dto.setSpecies(req.species() != null ? req.species() : existing.getSpecies());
        dto.setBreed(req.breed() != null ? req.breed() : existing.getBreed());
        dto.setAge(req.age() != null ? req.age() : existing.getAge());

        try {
            Pet updated = petService.updatePet(id, user.getId(), dto);
            return toResponse(updated);
        } catch (ValidationException ex) {
            String msg = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
            if (msg.contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
            }
            if (msg.contains("unauthorized")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    /**
     * Request DTO για create pet
     * Validation: ownerId/name/species required, age >= 0.
     */
    public record CreatePetRequest(
            @NotBlank String name,
            @NotBlank String species,
            String breed,
            @Min(value = 1, message = "Age must be greater than 0") int age
    ) {}

    /**
     * Request DTO for PATCH (όλα τα πεδία είναι optional)
     * Validation εφαρμόζεται μόνο αν δοθεί age.
     */
    public record UpdatePetRequest(
            String name,
            String species,
            String breed,
            @Min(value = 1, message = "Age must be greater than 0") Integer age
    ) {}
}
