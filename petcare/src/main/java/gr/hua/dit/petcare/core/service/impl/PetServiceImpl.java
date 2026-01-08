package gr.hua.dit.petcare.core.service.impl;

import gr.hua.dit.petcare.core.dto.PetDto;

import gr.hua.dit.petcare.core.model.Appointment;
import gr.hua.dit.petcare.core.model.AppointmentStatus;
import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Pet;

import gr.hua.dit.petcare.core.repository.AppointmentRepository;
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
    private final AppointmentRepository appointmentRepository;

    public PetServiceImpl(PetRepository petRepository,
                          UserRepository userRepository,
                          AppointmentRepository appointmentRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
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
        return petRepository.findByOwnerIdAndActiveTrue(ownerId);
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

        // ακύρωση μελλοντικών ραντεβού
        List<Appointment> toCancel =
                appointmentRepository.findByPetIdAndStatusIn(
                        petId,
                        List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED)
                );

        for (Appointment a : toCancel) {
            a.setStatus(AppointmentStatus.CANCELLED);
        }

        appointmentRepository.saveAll(toCancel);

        pet.setActive(false); // soft delete pet
        petRepository.save(pet);
    }

    /**
     * Εύρεση κατοικίδιου με την χρήση του id.
     */
    @Override
    public Pet getPetById(Long id) {
        Pet pet = petRepository.findById(id).orElse(null);
        return (pet != null && pet.isActive()) ? pet : null;
    }
}
