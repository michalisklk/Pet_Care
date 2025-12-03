package gr.hua.dit.petcare.core.service.impl;

import gr.hua.dit.petcare.core.dto.PetDto;

import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Pet;

import gr.hua.dit.petcare.core.repository.PetRepository;
import gr.hua.dit.petcare.core.repository.UserRepository;

import gr.hua.dit.petcare.core.service.PetService;

import jakarta.validation.ValidationException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public PetServiceImpl(PetRepository petRepository,
                          UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    /**
     * Δημιουργία κατοικίδιου για συγκεκριμένο owner
     */
    @Override
    public Pet createPet(Long ownerId, PetDto dto) {

        Person owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ValidationException("Owner not found"));

        // Δημιουργία entity Pet
        Pet pet = new Pet(
                dto.getName(),
                dto.getSpecies(),
                dto.getBreed(),
                dto.getAge(),
                owner
        );

        return petRepository.save(pet);
    }

    /**
     * Λίστα με όλα τα κατοικίδια του owner
     */
    @Override
    public List<Pet> getPetsForOwner(Long ownerId) {
        return petRepository.findByOwnerId(ownerId);
    }

    /**
     * Επεξεργασία των στοιχείων του κατοικίδιου
     */
    @Override
    public Pet updatePet(Long petId, Long ownerId, PetDto dto) {

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ValidationException("Pet not found"));

        // έλεγχος αν το pet ανήκει στον owner
        if (!pet.getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Unauthorized operation: This pet does not belong to this owner.");
        }

        // ενημέρωση των στοιχείων
        pet.setName(dto.getName());
        pet.setSpecies(dto.getSpecies());
        pet.setBreed(dto.getBreed());
        pet.setAge(dto.getAge());

        return petRepository.save(pet);
    }

    /**
     * Διαγραφή κατοικίδιου.
     */
    @Override
    public void deletePet(Long petId, Long ownerId) {

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ValidationException("Pet not found"));

        if (!pet.getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Unauthorized operation: This pet does not belong to this owner.");
        }

        petRepository.delete(pet);
    }

    /**
     * Εύρεση κατοικίδιου με την χρήση του id.
     */
    @Override
    public Pet getPetById(Long id) {
        return petRepository.findById(id).orElse(null);
    }
}
