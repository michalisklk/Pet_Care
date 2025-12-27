package gr.hua.dit.petcare.core.service;

import gr.hua.dit.petcare.core.model.Appointment;
import gr.hua.dit.petcare.core.model.AppointmentStatus;
import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Pet;
import gr.hua.dit.petcare.core.model.Role;
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
                                         String reason) {



        Person owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found"));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        Person vet = userRepository.findById(vetId)
                .orElseThrow(() -> new EntityNotFoundException("Vet not found"));
        if (start == null) {
            throw new IllegalStateException("Start time is required");
        }
        LocalDateTime end = start.plusMinutes(30);


        if (vet.getRole() != Role.VET) {
            throw new IllegalStateException("Selected user is not a vet");
        }

        if (pet.getOwner() == null || pet.getOwner().getId() == null) {
            throw new IllegalStateException("Pet has no owner");
        }
        if (!pet.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("Το κατοικίδιο δεν ανήκει σε αυτόν τον ιδιοκτήτη");
        }

        List<Appointment> overlaps = appointmentRepository.findOverlappingForVet(vet, start, end);
        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("Ο κτηνίατρος έχει ήδη ραντεβού σε αυτό το χρονικό διάστημα");
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


    // Λίστες ραντεβού
    // Owner βλέπει τα δικά του ραντεβού (με βάση ownerId)
    public List<Appointment> getAppointmentsForOwner(Long ownerId) {
        Person owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found"));

        return appointmentRepository.findByPetOwner(owner);
    }

    // Ο Vet βλέπει ολα τα δικά του ραντεβού όλων των status με βάση vetId
    public List<Appointment> getAppointmentsForVet(Long vetId) {
        // Βρίσκουμε τον vet από τη βάση.
        Person vet = userRepository.findById(vetId)
                .orElseThrow(() -> new EntityNotFoundException("Vet not found"));
        // Επιστρέφουμε όλα τα ραντεβού που έχουν αυτόν τον vet.

        return appointmentRepository.findByVet(vet);
    }
    // Vet βλέπει  τα δικά του pending ραντεβού με βάση vetId
    public List<Appointment> getPendingAppointmentsForVet(Long vetId) {
        //έλεγχος ότι υπάρχει ο vet(optional)
        userRepository.findById(vetId).orElseThrow(() -> new EntityNotFoundException("Vet not found"));
        // Επιστρέφουμε  τα ραντεβού του vet που έχουν ειναι pending και τα ταξινομούνται  με βάση το startTime(αύξουσα σειρά).
        return appointmentRepository.findByVetIdAndStatusOrderByStartTimeAsc(vetId, AppointmentStatus.PENDING);
    }

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
    //Φερνει  ένα συγκεκριμένο appointment και ελεγχει  ότι ανήκει στον vet
    private Appointment getAppointmentForVet(Long id, Person vet) {
        // Βρίσκουμε το appointment από τη βάση με βάση το appointmentId
        Appointment a = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        //ελεγχος αν υπαρχει ο vet
        if (a.getVet() == null || a.getVet().getId() == null) {
            throw new IllegalStateException("Appointment has no vet assigned");
        }

        if (!a.getVet().getId().equals(vet.getId())) {
            throw new IllegalStateException("You are not the vet for this appointment");
        }
        // Αν όλα είναι ενταξει επιστρεφεται το appointment
        return a;
    }


}
