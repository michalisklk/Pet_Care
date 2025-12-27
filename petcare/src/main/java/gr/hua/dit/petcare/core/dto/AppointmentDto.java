package gr.hua.dit.petcare.core.dto;

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

    // Προαιρετικό κείμενο
    @Size(max = 500, message = "Reason must be at most 500 characters")
    private String reason;

    public AppointmentDto() {}

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }

    public Long getVetId() { return vetId; }
    public void setVetId(Long vetId) { this.vetId = vetId; }

    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
