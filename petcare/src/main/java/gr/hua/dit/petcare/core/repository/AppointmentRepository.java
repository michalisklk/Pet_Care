package gr.hua.dit.petcare.core.repository;
import gr.hua.dit.petcare.core.model.Appointment;
import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // για ιδιοκτήτη
    List<Appointment> findByOwner(Person owner);

    // για κτηνίατρο
    List<Appointment> findByVet(Person vet);

    // έλεγχος αν ο κτηνίατρος έχει άλλο ραντεβού που επικαλύπτεται
    @Query("""
           select a from Appointment a
           where a.vet = :vet
             and a.status <> gr.hua.dit.petcare.core.model.AppointmentStatus.CANCELLED
             and (a.startTime < :endTime and a.endTime > :startTime)
           """)
    List<Appointment> findOverlappingForVet(@Param("vet") Person vet,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    //ελάχιστος χρόνος μεταξύ ραντεβού για ίδιο pet
    List<Appointment> findByPetAndStartTimeBetween(Pet pet,
                                                   LocalDateTime from,
                                                   LocalDateTime to);

    Person owner_Id(Long ownerId);
}

