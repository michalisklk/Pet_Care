package gr.hua.dit.petcare.core.service;


import gr.hua.dit.petcare.core.model.Appointment;
import gr.hua.dit.petcare.core.model.AppointmentStatus;
import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Pet;
import gr.hua.dit.petcare.core.repository.AppointmentRepository;
import gr.hua.dit.petcare.core.repository.PetRepository;
import gr.hua.dit.petcare.core.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PetRepository petRepository,
                              UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    // Δημιουργία ραντεβού από owner
    public Appointment createAppointment(Long ownerId,
                                         Long petId,
                                         Long vetId,
                                         LocalDateTime start,
                                         LocalDateTime end,
                                         String reason) {

        Person owner = userRepository.findById(ownerId).orElseThrow(() -> new EntityNotFoundException("Owner not found"));

        Pet pet = petRepository.findById(petId).orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        Person vet = userRepository.findById(vetId).orElseThrow(() -> new EntityNotFoundException("Vet not found"));

        //Έλεγχος ότι το pet ανήκει στον owner
        if (!pet.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("Το κατοικίδιο δεν ανήκει σε αυτόν τον ιδιοκτήτη");
        }

        Appointment appointment = new Appointment(
                pet,
                vet,
                start,
                end,
                reason
        );
        appointment.setStatus(AppointmentStatus.PENDING);

        return appointmentRepository.save(appointment);
    }

//Δεν τα χρησιμοποιώ ακόμα άλλα θεωρώ οτι θα χρειαστούν σίγουρα μέτα
    // Λίστες ραντεβού
    // Owner βλέπει τα δικά του ραντεβού (με βάση ownerId)
    public List<Appointment> getAppointmentsForOwner(Long ownerId) {
        return userRepository.findById(ownerId)
                .map(appointmentRepository::findByOwner)
                .orElse(List.of()); // αν δεν βρεθεί owner, άδεια λίστα
    }

    // Vet βλέπει τα δικά του ραντεβού (με βάση vetId)
    public List<Appointment> getAppointmentsForVet(Long vetId) {
        return userRepository.findById(vetId)
                .map(appointmentRepository::findByVet)
                .orElse(List.of());
    }

    // vet ραντεβού
    public Appointment confirm(Long appointmentId, Person vet) {
        Appointment a = getAppointmentForVet(appointmentId, vet);
        a.setStatus(AppointmentStatus.CONFIRMED);
        return appointmentRepository.save(a);
    }

    public Appointment cancelAsVet(Long appointmentId, Person vet, String notes) {
        Appointment a = getAppointmentForVet(appointmentId, vet);
        a.setStatus(AppointmentStatus.CANCELLED);
        a.setVetNotes(notes);
        return appointmentRepository.save(a);
    }

    public Appointment complete(Long appointmentId, Person vet, String notes) {
        Appointment a = getAppointmentForVet(appointmentId, vet);
        a.setStatus(AppointmentStatus.COMPLETED);
        a.setVetNotes(notes);
        return appointmentRepository.save(a);
    }

    // σιγουρεύεται ότι ο vet είναι πράγματι ο vet του ραντεβού
    private Appointment getAppointmentForVet(Long id, Person vet) {
        Appointment a = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        if (!a.getVet().getId().equals(vet.getId())) {
            throw new IllegalStateException("You are not the vet for this appointment");
        }
        return a;
    }
}

