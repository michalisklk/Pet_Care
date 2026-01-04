package gr.hua.dit.petcare.web.api.dto;

import gr.hua.dit.petcare.core.model.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long petId,
        Long ownerId,
        Long vetId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status,
        String reason,
        String vetNotes
) {}
