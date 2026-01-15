package gr.hua.dit.notification_service.core.impl;

import gr.hua.dit.notification_service.core.VaccineCatalogService;
import gr.hua.dit.notification_service.core.model.VaccineInfo;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Mock vaccine catalog
 * Goal: keep texts short, clear, and consistent for the UI.
 * Note: this is NOT medical advice.
 */
@Service
public class InMemoryVaccineCatalogService implements VaccineCatalogService {

    // Map<"DOG", List<VaccineInfo>> κλπ
    private final Map<String, List<VaccineInfo>> catalog;

    public InMemoryVaccineCatalogService() {
        Map<String, List<VaccineInfo>> m = new LinkedHashMap<>();

        // βάζουμε demo δεδομένα (mock). Τα keys είναι uppercase.
        m.put("DOG", List.of(
                v("DHPP", "DHPP (core dog vaccine)",
                        "Usually given as a series of shots when the dog is young. Ask your vet for the schedule."),
                v("RABIES", "Rabies",
                        "Often required by law. Your vet will tell you when it is due."),
                v("BORDETELLA", "Bordetella (kennel cough)",
                        "Optional. Useful if the dog goes to daycare/boarding or meets many other dogs.")
        ));

        m.put("CAT", List.of(
                v("FVRCP", "FVRCP (core cat vaccine)",
                        "Usually given as a series of shots when the cat is young. Ask your vet for the schedule."),
                v("RABIES", "Rabies",
                        "Required in some areas. Ask your vet what applies in your region."),
                v("FELV", "FeLV",
                        "Often recommended for outdoor cats or cats that meet other cats.")
        ));

        m.put("HAMSTER", List.of(
                v("N/A", "No routine vaccines",
                        "Routine vaccines are uncommon. If you are worried about symptoms, contact a vet.")
        ));

        m.put("RABBIT", List.of(
                v("MYXO", "Myxomatosis",
                        "Depends on the region and risk. Ask your vet if it is recommended where you live."),
                v("RHDV", "RHDV",
                        "Depends on local outbreaks and guidance. Ask your vet for the right schedule.")
        ));

        m.put("BIRD", List.of(
                v("POLYOMA", "Polyomavirus",
                        "Species-specific. An avian vet can advise if it is needed."),
                v("PACHECO", "Pacheco's disease",
                        "Mainly discussed for some parrots. Ask an avian vet.")
        ));

        // unmodifiable για να μην αλλάζει κατά λάθος από κάπου αλλού
        this.catalog = Collections.unmodifiableMap(m);
    }

    @Override
    public List<VaccineInfo> typicalVaccinesFor(String species) {
        String key = normalize(species);
        List<VaccineInfo> vaccines = catalog.get(key);

        // αν δεν υπάρχει species στο map -> πετάμε error με helpful μήνυμα
        if (vaccines == null) {
            throw new IllegalArgumentException(
                    "Unsupported species '" + species + "'. Use one of: "
                            + String.join(", ", supportedSpecies())
                            + ". (Tip: GET /api/v1/vaccines/supported-species)"
            );
        }
        return vaccines;
    }

    @Override
    public List<String> supportedSpecies() {
        // επιστρέφουμε τα keys (DOG, CAT, ......)
        return List.copyOf(catalog.keySet());
    }

    private static VaccineInfo v(String code, String name, String notes) {
        // μικρό helper για να μη γράφουμε συνέχεια new VaccineInfo(...)
        return new VaccineInfo(code, name, notes);
    }

    private static String normalize(String species) {
        // καθαρίζουμε input. Θέλουμε πάντα uppercase για match με keys.
        if (species == null || species.trim().isEmpty()) {
            throw new IllegalArgumentException("species is required");
        }
        return species.trim().toUpperCase(Locale.ROOT);
    }
}
