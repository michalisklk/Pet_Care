package gr.hua.dit.petcare.core.service;

import gr.hua.dit.petcare.core.model.*;
import gr.hua.dit.petcare.core.repository.AppointmentRepository;
import gr.hua.dit.petcare.core.repository.PetRepository;
import gr.hua.dit.petcare.core.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    private final AppointmentNotificationService appointmentNotificationService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PetRepository petRepository,
                              UserRepository userRepository,
                              AppointmentNotificationService appointmentNotificationService) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.appointmentNotificationService = appointmentNotificationService;
    }

    // Δημιουργία ραντεβού από owner
    public Appointment createAppointment(Long ownerId,
                                         Long petId,
                                         Long vetId,
                                         LocalDateTime start,
                                         AppointmentReason reason) {

        Person owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found"));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        Person vet = userRepository.findById(vetId)
                .orElseThrow(() -> new EntityNotFoundException("Vet not found"));

        if (start == null) {
            throw new IllegalStateException("Start time is required");
        }
        if (reason == null) {
            throw new IllegalStateException("Reason is required");
        }

        LocalDateTime now = LocalDateTime.now();

        // ο χρήστης δεν θα μπορεί να κλείσει ραντεβού στο παρελθόν
        if (start.isBefore(now)) {
            throw new IllegalStateException("Δεν μπορείς να κλείσεις ραντεβού στο παρελθόν");
        }

        //Ο χρήστης θα πρέπει αν Κλείνει ραντεβού Μέτα απο μια ωρα καθώς δεν γίνεται να κλάσει ραντεβού για εκείνο ακριβώς τον χρόνο.
        if (start.isBefore(now.plusHours(1))) {
            throw new IllegalStateException("Το ραντεβού πρέπει να κλείνεται τουλάχιστον 1 ωρα μετά από τώρα");
        }

        //διάρκειά κάθε ραντεβού 30 Είναι λεπτά
        LocalDateTime end = start.plusMinutes(30);

        if (vet.getRole() != Role.VET) {
            throw new IllegalStateException("Selected user is not a vet");
        }

        // το pet πρέπει να ανήκει στον owner
        if (pet.getOwner() == null || pet.getOwner().getId() == null) {
            throw new IllegalStateException("Pet has no owner");
        }
        if (!pet.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("Το κατοικίδιο δεν ανήκει σε αυτόν τον ιδιοκτήτη");
        }

        // Δεν επιτρέπεται overlap στον ίδιο vet
        List<Appointment> overlaps = appointmentRepository.findOverlappingForVet(vet, start, end);
        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("Ο κτηνίατρος έχει ήδη ραντεβού σε αυτό το χρονικό διάστημα");
        }

        //Ίδιο σημαντικό reason για ίδιο pet -> όχι νέο αν το προηγούμενο δεν έγινε COMPLETED επίσης αν είναι COMPLETED,
        //εφαρμόζουμε cooldown ανάλογά με το reason (reasons οχι τόσο σημαντικά δεν έχουν cooldown)
        NextImportantAppointment(pet, reason, start);

        Appointment appointment = new Appointment(
                pet,
                vet,
                start,
                end,
                reason
        );
        appointment.setStatus(AppointmentStatus.PENDING);

        //Σώζουμε
        Appointment saved = appointmentRepository.save(appointment);

        //Trigger notifications (SSE + SMS)
        appointmentNotificationService.onCreated(saved);

        return saved;
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

        Appointment saved = appointmentRepository.save(a);

        // Trigger notifications
        appointmentNotificationService.onConfirmed(saved);

        return saved;
    }

    public Appointment cancelAsVet(Long appointmentId, Person vet, String notes) {
        Appointment a = getAppointmentForVet(appointmentId, vet);
        a.setStatus(AppointmentStatus.CANCELLED);
        a.setVetNotes(notes);

        Appointment saved = appointmentRepository.save(a);

        // Trigger notifications
        appointmentNotificationService.onCancelled(saved);

        return saved;
    }

    public Appointment complete(Long appointmentId, Person vet, String notes) {
        Appointment a = getAppointmentForVet(appointmentId, vet);
        a.setStatus(AppointmentStatus.COMPLETED);
        a.setVetNotes(notes);

        Appointment saved = appointmentRepository.save(a);

        // Trigger notifications
        appointmentNotificationService.onCompleted(saved);

        return saved;
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

    // πόσο cooldown έχει κάθε σημαντική κατηγορία
    private Period CalculateTime(AppointmentReason reason) {
        if (reason == AppointmentReason.VACCINES) {
            return Period.ofMonths(1);
        }
        if (reason == AppointmentReason.SURGERY) {
            return Period.ofMonths(6);
        }
        if (reason == AppointmentReason.DIET) {
            return Period.ofWeeks(2);
        }
        return Period.ZERO;
    }

    // ίδιο σημαντικό reason για ίδιο pet -> όχι νέο αν το προηγούμενο δεν έγινε COMPLETED
    // αν είναι COMPLETED, εφαρμόζουμε cooldown (Μονό τα σημαντικά ραντεβού έχουν cooldown)
    private void NextImportantAppointment(Pet pet,
                                          AppointmentReason reason,
                                          LocalDateTime newStart) {

        Period cooldown = CalculateTime(reason);

        //αγνοούμε ta CANCELLED ραντεβού
        appointmentRepository
                .findFirstByPetAndReasonAndStatusNotOrderByStartTimeDesc(pet, reason, AppointmentStatus.CANCELLED)
                .ifPresent(last -> {

                    // αν υπάρχει προηγούμενο ίδιο reason και δεν ολοκληρώθηκε, δεν αφήνουμε τον χρήστη να κλείσει άλλο
                    if (last.getStatus() != AppointmentStatus.COMPLETED) {
                        throw new IllegalStateException(
                                "There is already a " + reason + " appointment in progress (date: "
                                        + last.getStartTime() + " It must be completed first."
                        );
                    }

                    if (cooldown.isZero()) return;

                    // cooldown με βάση το startTime του τελευταίου COMPLETED
                    LocalDateTime allowedFrom = last.getStartTime().plus(cooldown);
                    if (newStart.isBefore(allowedFrom)) {
                        throw new IllegalStateException(
                                "You cannot book another " + reason +
                                        " appointment before the required cooldown period has passed (" + cooldown + ")."
                        );
                    }
                });
    }
}
