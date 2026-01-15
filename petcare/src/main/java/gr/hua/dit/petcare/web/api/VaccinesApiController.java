package gr.hua.dit.petcare.web.api;

import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Role;
import gr.hua.dit.petcare.core.service.VaccineSuggestionService;
import gr.hua.dit.petcare.notification.dto.VaccineInfo;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Vaccines",
        description = "Typical vaccines by species (served via external vaccine catalog service)."
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1")
@Validated
public class VaccinesApiController {

    private final VaccineSuggestionService vaccineSuggestionService;

    public VaccinesApiController(VaccineSuggestionService vaccineSuggestionService) {
        this.vaccineSuggestionService = vaccineSuggestionService;
    }

    /**
     * GET /api/v1/vaccines/typical?species=Dog
     * αυτό δίνει list τυπικών εμβολίων μόνο από species (χωρίς pet).
     */
    @Operation(
            summary = "Typical vaccines by species",
            description = "Returns an indicative list of typical vaccines for the given species. This is not medical advice."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    @GetMapping("/vaccines/typical")
    public List<VaccineInfo> typicalBySpecies(
            @AuthenticationPrincipal Person user,
            @RequestParam @NotBlank String species
    ) {
        requireUser(user); // Φοιτητικό: basic check
        return vaccineSuggestionService.typicalVaccinesForSpecies(species);
    }

    /**
     * GET /api/v1/pets/{petId}/vaccines/typical
     * owner-only endpoint: παίρνει το pet, τσεκάρει ότι ανήκει στον owner, και μετά βρίσκει vaccines.
     */
    @Operation(
            summary = "Typical vaccines for a pet",
            description = "Owner-only. Uses the pet's species and calls the external vaccine catalog service. This is not medical advice."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized"),
            @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    @GetMapping("/pets/{petId}/vaccines/typical")
    public List<VaccineInfo> typicalForPet(
            @AuthenticationPrincipal Person user,
            @PathVariable Long petId
    ) {
        requireUser(user);

        // vet δεν επιτρέπεται εδώ (μόνο owner)
        if (user.getRole() == Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only pet owners can access this endpoint");
        }

        // service πετάει ValidationException με message, εδώ το κάνουμε map σε σωστό HTTP status
        try {
            return vaccineSuggestionService.typicalVaccinesForPet(petId, user.getId());
        } catch (ValidationException ex) {
            throw mapValidationException(ex);
        }
    }

    private static void requireUser(Person user) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
    }

    /**
     * μεταφράζουμε το ValidationException message -> σε 404/403/400.
     */
    private static ResponseStatusException mapValidationException(ValidationException ex) {
        String msg = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();

        if (msg.contains("not found")) {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found");
        }
        if (msg.contains("unauthorized")) {
            return new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this pet");
        }
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
    }
}
