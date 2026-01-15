package gr.hua.dit.petcare.notification.adapter;

import gr.hua.dit.petcare.notification.dto.VaccineInfo;
import gr.hua.dit.petcare.notification.dto.VaccineListResponse;
import gr.hua.dit.petcare.notification.port.VaccineCatalogPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class NocVaccineCatalogAdapter implements VaccineCatalogPort {

    private final RestTemplate restTemplate;
    private final String vaccinesBaseUrl;

    public NocVaccineCatalogAdapter(RestTemplate restTemplate,
                                    @Value("${external.vaccines-service.url}") String vaccinesBaseUrl) {
        this.restTemplate = restTemplate;
        this.vaccinesBaseUrl = vaccinesBaseUrl;
    }

    @Override
    public List<VaccineInfo> typicalVaccinesForSpecies(String species) {
        // φτιάχνουμε endpoint: baseUrl + "/DOG" (normalized)
        String url = vaccinesBaseUrl + "/" + normalizeSpecies(species);

        try {
            // παίρνουμε response wrapper, μετά παίρνουμε μόνο τη λίστα
            VaccineListResponse response = restTemplate.getForObject(url, VaccineListResponse.class);
            return (response == null || response.vaccines() == null) ? Collections.emptyList() : response.vaccines();
        } catch (Exception e) {
            // graceful fallback = αν το external service πέσει,
            // δεν θέλουμε να πέσει όλη η PetCare -> επιστρέφουμε empty list
            return Collections.emptyList();
        }
    }

    private static String normalizeSpecies(String species) {
        // έλεγχος input, για να μην χτίσουμε λάθος URL
        if (species == null || species.trim().isEmpty()) throw new IllegalArgumentException("species is required");
        return species.trim().toUpperCase(Locale.ROOT);
    }
}
