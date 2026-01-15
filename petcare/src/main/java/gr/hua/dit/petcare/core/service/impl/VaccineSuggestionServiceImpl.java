package gr.hua.dit.petcare.core.service.impl;

import gr.hua.dit.petcare.core.model.Pet;
import gr.hua.dit.petcare.core.repository.PetRepository;
import gr.hua.dit.petcare.core.service.VaccineSuggestionService;
import gr.hua.dit.petcare.notification.dto.VaccineInfo;
import gr.hua.dit.petcare.notification.port.VaccineCatalogPort;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VaccineSuggestionServiceImpl implements VaccineSuggestionService {

    private final PetRepository petRepository;
    private final VaccineCatalogPort vaccineCatalogPort;

    public VaccineSuggestionServiceImpl(PetRepository petRepository, VaccineCatalogPort vaccineCatalogPort) {
        this.petRepository = petRepository;
        this.vaccineCatalogPort = vaccineCatalogPort;
    }

    @Override
    public List<VaccineInfo> typicalVaccinesForSpecies(String species) {
        return vaccineCatalogPort.typicalVaccinesForSpecies(species);
    }

    @Override
    public List<VaccineInfo> typicalVaccinesForPet(Long petId, Long ownerId) {
        // βρίσκουμε το pet από DB
        Pet pet = petRepository.findById(petId).orElse(null);

        // αν δεν υπάρχει ή είναι inactive -> "Pet not found"
        if (pet == null || !pet.isActive()) throw new ValidationException("Pet not found");

        // authorization check (ownerId πρέπει να ταιριάζει με pet.owner.id)
        if (pet.getOwner() == null || ownerId == null || !pet.getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Unauthorized operation: This pet does not belong to this owner.");
        }

        // τελικά κάνουμε call με βάση το species του pet
        return vaccineCatalogPort.typicalVaccinesForSpecies(pet.getSpecies());
    }
}
