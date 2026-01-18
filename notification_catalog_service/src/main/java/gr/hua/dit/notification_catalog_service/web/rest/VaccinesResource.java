package gr.hua.dit.notification_catalog_service.web.rest;

import gr.hua.dit.notification_catalog_service.core.VaccineCatalogService;
import gr.hua.dit.notification_catalog_service.core.model.VaccineInfo;
import gr.hua.dit.notification_catalog_service.core.model.VaccineListResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(value = "/api/v1/vaccines", produces = MediaType.APPLICATION_JSON_VALUE)
public class VaccinesResource {

    private final VaccineCatalogService vaccineCatalogService;

    public VaccinesResource(VaccineCatalogService vaccineCatalogService) {
        this.vaccineCatalogService = vaccineCatalogService;
    }

    /**
     * GET /api/v1/vaccines/{species}
     * επιστρέφει wrapper με (normalized species + list vaccines)
     */
    @GetMapping("/{species}")
    public ResponseEntity<VaccineListResponse> typicalVaccinesBySpecies(@PathVariable String species) {
        // service πετάει IllegalArgumentException αν species δεν υποστηρίζεται
        List<VaccineInfo> vaccines = vaccineCatalogService.typicalVaccinesFor(species);


        String normalized = (species == null) ? null : species.trim().toUpperCase(Locale.ROOT);

        return ResponseEntity.ok(new VaccineListResponse(normalized, vaccines));
    }

    /**
     * GET /api/v1/vaccines/supported-species
     * βοηθητικό endpoint για να ξέρει ο client τι species υποστηρίζονται.
     */
    @GetMapping("/supported-species")
    public ResponseEntity<List<String>> supportedSpecies() {
        return ResponseEntity.ok(vaccineCatalogService.supportedSpecies());
    }
}
