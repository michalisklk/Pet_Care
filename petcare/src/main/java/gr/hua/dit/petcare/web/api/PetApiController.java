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
     * Request DTO για create pet
     * Validation: ownerId/name/species required, age >= 0.
     */
    public record CreatePetRequest(
            @NotNull String name,
            @NotNull String species,
            String breed,
            int age
    ) {}

}
