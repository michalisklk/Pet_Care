package gr.hua.dit.petcare.web.api;

import gr.hua.dit.petcare.core.model.Appointment;
import gr.hua.dit.petcare.core.model.AppointmentReason;
import gr.hua.dit.petcare.core.service.AppointmentService;
import gr.hua.dit.petcare.web.api.dto.AppointmentResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentApiController {

    private final AppointmentService appointmentService;

    public AppointmentApiController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * POST /api/v1/appointments
     * Body: { "ownerId":1, "petId":2, "vetId":3, "start":"2026-01-22T12:32", "reason":"VACCINES" }
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(@Valid @RequestBody CreateAppointmentRequest req) {
        Appointment saved = appointmentService.createAppointment(
                req.ownerId(),
                req.petId(),
                req.vetId(),
                req.start(),
                req.reason()
        );
        return toResponse(saved);
    }

    /**
     * GET /api/v1/appointments?ownerId=1
     * ή  GET /api/v1/appointments?vetId=3
     */
    @GetMapping
    public List<AppointmentResponse> list(
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Long vetId
    ) {
        if (ownerId != null) {
            return appointmentService.getAppointmentsForOwner(ownerId).stream()
                    .map(this::toResponse)
                    .toList();
        }
        if (vetId != null) {
            return appointmentService.getAppointmentsForVet(vetId).stream()
                    .map(this::toResponse)
                    .toList();
        }

        throw new IllegalArgumentException("Give either ownerId or vetId");
    }

    private AppointmentResponse toResponse(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getPet() != null ? a.getPet().getId() : null,
                a.getOwner() != null ? a.getOwner().getId() : null,
                a.getVet() != null ? a.getVet().getId() : null,
                a.getStartTime(),
                a.getEndTime(),
                a.getStatus(),
                a.getReason() != null ? a.getReason().name() : null, // enum to String
                a.getVetNotes()
        );
    }

    public record CreateAppointmentRequest(
            @NotNull Long ownerId,
            @NotNull Long petId,
            @NotNull Long vetId,
            @NotNull LocalDateTime start,
            @NotNull AppointmentReason reason
    ) {}
}
