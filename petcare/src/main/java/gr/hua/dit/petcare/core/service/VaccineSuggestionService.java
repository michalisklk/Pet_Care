package gr.hua.dit.petcare.core.service;

import gr.hua.dit.petcare.notification.dto.VaccineInfo;

import java.util.List;

public interface VaccineSuggestionService {

    List<VaccineInfo> typicalVaccinesForSpecies(String species);

    List<VaccineInfo> typicalVaccinesForPet(Long petId, Long ownerId);
}
