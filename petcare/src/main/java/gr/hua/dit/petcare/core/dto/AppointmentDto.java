package gr.hua.dit.petcare.core.dto;

import gr.hua.dit.petcare.core.model.AppointmentReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class AppointmentDto {

    //Ποιος είναι ο ιδιοκτήτης του ραντεβού
    @NotNull(message = "Owner is required")
    private Long ownerId;

    //Για ποιο pet Είναι το ραντεβού
    @NotNull(message = "Pet is required")
    private Long petId;

    //Ποιος κτηνίατρος θα δει το pet
    @NotNull(message = "Vet is required")
    private Long vetId;

    // Πότε ξεκινάει το ραντεβού
    @NotNull(message = "Start time is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime start;

    @NotNull
    private AppointmentReason reason;


    // Προαιρετικό κείμενο
    @Size(max = 500, message = "Reason must be at most 500 characters")
    private String otherreason;

    public AppointmentDto() {}

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }

    public Long getVetId() { return vetId; }
    public void setVetId(Long vetId) { this.vetId = vetId; }

    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }

    public AppointmentReason getReason() { return reason; }
    public void setReason(AppointmentReason reason) { this.reason = reason; }

    public String getOtherreason() {return otherreason;}

    public void setOtherreason(String otherreason) {this.otherreason = otherreason;}
}
