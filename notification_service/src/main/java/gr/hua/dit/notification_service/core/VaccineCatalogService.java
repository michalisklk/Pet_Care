package gr.hua.dit.notification_service.core;

import gr.hua.dit.notification_service.core.model.VaccineInfo;

import java.util.List;

/**
 * Mock catalog με τυπικά εμβόλια ανά είδος ζώου.
 */
public interface VaccineCatalogService {

    List<VaccineInfo> typicalVaccinesFor(String species);

    List<String> supportedSpecies();
}
