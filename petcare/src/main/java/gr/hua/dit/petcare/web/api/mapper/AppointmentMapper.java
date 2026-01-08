package gr.hua.dit.petcare.web.api.mapper;

import gr.hua.dit.petcare.core.model.Appointment;
import gr.hua.dit.petcare.web.api.dto.AppointmentResponse;

public class AppointmentMapper {

    private AppointmentMapper() {
        // utility class
    }

    public static AppointmentResponse toResponse(Appointment a) {
        if (a == null) return null;

        return new AppointmentResponse(
                a.getId(),
                a.getPet() != null ? a.getPet().getId() : null,
                a.getOwner() != null ? a.getOwner().getId() : null,
                a.getVet() != null ? a.getVet().getId() : null,
                a.getStartTime(),
                a.getEndTime(),
                a.getStatus(),
                a.getReason() != null ? a.getReason().name() : null,
                a.getVetNotes()
        );
    }
}
