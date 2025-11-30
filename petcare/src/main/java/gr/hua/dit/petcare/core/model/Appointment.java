package gr.hua.dit.petcare.core.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // κατοικίδιο
    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    // κτηνίατρος
    @ManyToOne(optional = false)
    @JoinColumn(name = "vet_id")
    private Person vet;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private Person owner;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    // λόγος επίσκεψης από ιδιοκτήτη
    @Column(length = 500)
    private String reason;

    // σημειώσεις / θεραπείες από κτηνίατρο
    @Column(length = 2000)
    private String vetNotes;

    public Appointment() { }//για το jpa

    public Appointment(Pet pet, Person vet,
                       LocalDateTime startTime, LocalDateTime endTime,
                       String reason) {
        this.pet = pet;
        this.vet = vet;
        this.owner = pet.getOwner();
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.status = AppointmentStatus.PENDING;
    }

    // getters / setters
    public Person getOwner() {
        return owner;
    }

    // αν ΘΕΛΕΙΣ οπωσδήποτε setter:
    public void setOwner(Person owner) {
        this.owner = owner;
    }
    public Long getId() {
        return id;
    }

    public Pet getPet() {
        return pet;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Person getVet() {
        return vet;
    }
    public void setVet(Person vet) {
        this.vet = vet;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getVetNotes() {
        return vetNotes;
    }
    public void setVetNotes(String vetNotes) {
        this.vetNotes = vetNotes;
    }
}

