package gr.hua.dit.petcare.core.repository;
import gr.hua.dit.petcare.core.model.Appointment;
import gr.hua.dit.petcare.core.model.AppointmentStatus;
import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Owner: βλέπει τα ραντεβού του μέσω των pets του.
     * Δηλαδή: appointment.pet.owner = owner
     */
    List<Appointment> findByPetOwner(Person owner);

    /**
     * Owner: ίδια λογική, αλλά με ownerId.
     * Δηλαδή: appointment.pet.owner.id = ownerId
     */
    List<Appointment> findByPetOwnerId(Long ownerId);

    /**
     * Vet: βλέπει τα ραντεβού όπου είναι ο ίδιος κτηνίατρος.
     */
    List<Appointment> findByVet(Person vet);

    /**
     * Έλεγχος overlap για συγκεκριμένο vet (για να μην κλείνουμε ραντεβού πάνω σε άλλο).
     * Συνθήκη overlap: (start < otherEnd) AND (end > otherStart)
     * Εξαιρούμε τα CANCELLED.
     */
    @Query("""
           select a from Appointment a
           where a.vet = :vet
             and a.status <> gr.hua.dit.petcare.core.model.AppointmentStatus.CANCELLED
             and (a.startTime < :endTime and a.endTime > :startTime)
           """)
    List<Appointment> findOverlappingForVet(@Param("vet") Person vet,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    /**
     * Επιστρέφει ραντεβού ενός pet μέσα σε χρονικό διάστημα (π.χ. για min gap rule).
     */
    List<Appointment> findByPetAndStartTimeBetween(Pet pet,
                                                   LocalDateTime from,
                                                   LocalDateTime to);
    List<Appointment> findByVetIdAndStatusOrderByStartTimeAsc(Long vetId, AppointmentStatus status);

}

