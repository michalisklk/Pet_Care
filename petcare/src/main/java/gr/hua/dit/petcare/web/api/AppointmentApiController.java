package gr.hua.dit.petcare.web.api;

import gr.hua.dit.petcare.core.model.Appointment;
import gr.hua.dit.petcare.core.model.AppointmentReason;
import gr.hua.dit.petcare.core.service.AppointmentService;
import gr.hua.dit.petcare.web.api.dto.AppointmentResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Role;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.server.ResponseStatusException;


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
    public AppointmentResponse create(@AuthenticationPrincipal Person user,
                                      @Valid @RequestBody CreateAppointmentRequest req) {

        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");

        // μόνο owner να δημιουργεί appointment
        if (user.getRole() == Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vets cannot create appointments");
        }

        Appointment saved = appointmentService.createAppointment(
                user.getId(),
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
    public List<AppointmentResponse> list(@AuthenticationPrincipal Person user) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");

        if (user.getRole() == Role.VET) {
            return appointmentService.getAppointmentsForVet(user.getId()).stream()
                    .map(this::toResponse)
                    .toList();
        }

        return appointmentService.getAppointmentsForOwner(user.getId()).stream()
                .map(this::toResponse)
                .toList();
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
            @NotNull Long petId,
            @NotNull Long vetId,
            @NotNull LocalDateTime start,
            @NotNull AppointmentReason reason
    ) {}

}
