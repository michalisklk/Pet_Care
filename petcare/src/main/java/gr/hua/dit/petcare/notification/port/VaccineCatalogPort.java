package gr.hua.dit.petcare.notification.port;

import gr.hua.dit.petcare.notification.dto.VaccineInfo;

import java.util.List;

public interface VaccineCatalogPort {
    List<VaccineInfo> typicalVaccinesForSpecies(String species);
}
