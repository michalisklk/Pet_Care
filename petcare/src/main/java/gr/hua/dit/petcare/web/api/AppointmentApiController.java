package gr.hua.dit.petcare.web.api;

import gr.hua.dit.petcare.core.model.Appointment;
import gr.hua.dit.petcare.core.model.AppointmentReason;
import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Role;
import gr.hua.dit.petcare.core.service.AppointmentService;
import gr.hua.dit.petcare.web.api.dto.AppointmentResponse;
import gr.hua.dit.petcare.web.api.mapper.AppointmentMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

// Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Appointments", description = "Δημιουργία και διαχείριση ραντεβού")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentApiController {

    private final AppointmentService appointmentService;

    public AppointmentApiController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * POST /api/v1/appointments
     * Body: { "petId":2, "vetId":3, "start":"2026-12-12T22:33:00", "reason":"VACCINES" }
     */
    @Operation(summary = "Δημιουργία ραντεβού", description = "Ο owner δημιουργεί ραντεβού για pet και vet.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Το ραντεβού δημιουργήθηκε"),
            @ApiResponse(responseCode = "401", description = "Δεν είναι logged in"),
            @ApiResponse(responseCode = "403", description = "Vet δεν μπορεί να δημιουργήσει ραντεβού")
    })
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

        return AppointmentMapper.toResponse(saved);
    }

    @Operation(summary = "Λίστα ραντεβού", description = "Owner: τα ραντεβού του. Vet: τα ραντεβού του.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Επιστροφή λίστας ραντεβού"),
            @ApiResponse(responseCode = "401", description = "Δεν είναι logged in")
    })
    @GetMapping
    public List<AppointmentResponse> list(@AuthenticationPrincipal Person user) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");

        if (user.getRole() == Role.VET) {
            return appointmentService.getAppointmentsForVet(user.getId()).stream()
                    .map(AppointmentMapper::toResponse)
                    .toList();
        }

        return appointmentService.getAppointmentsForOwner(user.getId()).stream()
                .map(AppointmentMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Pending ραντεβού (Vet)", description = "Ο vet βλέπει τα pending ραντεβού του.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Επιστροφή λίστας pending ραντεβού"),
            @ApiResponse(responseCode = "401", description = "Δεν είναι logged in"),
            @ApiResponse(responseCode = "403", description = "Μόνο vet")
    })
    @GetMapping("/vet/pending")
    public List<AppointmentResponse> vetPending(@AuthenticationPrincipal Person user) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        if (user.getRole() != Role.VET) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets");

        return appointmentService.getPendingAppointmentsForVet(user.getId()).stream()
                .map(AppointmentMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Canceled ραντεβού (Vet)", description = "Ο vet βλέπει τα cancel ραντεβού του.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Επιστροφή λίστας Cancel ραντεβού"),
            @ApiResponse(responseCode = "401", description = "Δεν είναι logged in"),
            @ApiResponse(responseCode = "403", description = "Μόνο vet")
    })
    @GetMapping("/vet/cancel")
    public List<AppointmentResponse> vetCancel(@AuthenticationPrincipal Person user) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        if (user.getRole() != Role.VET) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets");

        return appointmentService.getCancelledAppointmentsForVet(user.getId()).stream()
                .map(AppointmentMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Confirm ραντεβού (Vet)", description = "Ο vet βλέπει τα confirm ραντεβού του.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Επιστροφή λίστας Confirm ραντεβού"),
            @ApiResponse(responseCode = "401", description = "Δεν είναι logged in"),
            @ApiResponse(responseCode = "403", description = "Μόνο vet")
    })
    @GetMapping("/vet/confirm")
    public List<AppointmentResponse> vetConfirm(@AuthenticationPrincipal Person user) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        if (user.getRole() != Role.VET) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets");

        return appointmentService.getConfirmedAppointmentsForVet(user.getId()).stream()
                .map(AppointmentMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Complete ραντεβού (Vet)", description = "Ο vet βλέπει τα complete ραντεβού του.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Επιστροφή λίστας  complete ραντεβού"),
            @ApiResponse(responseCode = "401", description = "Δεν είναι logged in"),
            @ApiResponse(responseCode = "403", description = "Μόνο vet")
    })
    @GetMapping("/vet/complete")
    public List<AppointmentResponse> vetComplete(@AuthenticationPrincipal Person user) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        if (user.getRole() != Role.VET) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets");

        return appointmentService.getCompletedAppointmentsForVet(user.getId()).stream()
                .map(AppointmentMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Confirm ραντεβού (Vet)", description = "Ο vet επιβεβαιώνει ένα pending ραντεβού.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Το ραντεβού επιβεβαιώθηκε"),
            @ApiResponse(responseCode = "401", description = "Δεν είναι logged in"),
            @ApiResponse(responseCode = "403", description = "Μόνο vet"),
            @ApiResponse(responseCode = "404", description = "Δεν βρέθηκε ραντεβού")
    })
    @PatchMapping("/{id}/confirm")
    public AppointmentResponse confirm(@AuthenticationPrincipal Person user, @PathVariable Long id) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        if (user.getRole() != Role.VET) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets can confirm");

        Appointment updated = appointmentService.confirm(id, user);
        return AppointmentMapper.toResponse(updated);
    }

    @Operation(summary = "Cancel ραντεβού (Vet)", description = "Ο vet ακυρώνει ραντεβού και μπορεί να βάλει σημείωση.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Το ραντεβού ακυρώθηκε"),
            @ApiResponse(responseCode = "401", description = "Δεν είναι logged in"),
            @ApiResponse(responseCode = "403", description = "Μόνο vet"),
            @ApiResponse(responseCode = "404", description = "Δεν βρέθηκε ραντεβού")
    })
    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(@AuthenticationPrincipal Person user,
                                      @PathVariable Long id,
                                      @Valid @RequestBody NotesRequest req) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        if (user.getRole() != Role.VET) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets can cancel");

        Appointment updated = appointmentService.cancelAsVet(id, user, req.notes());
        return AppointmentMapper.toResponse(updated);
    }

    @Operation(summary = "Complete ραντεβού (Vet)", description = "Ο vet ολοκληρώνει ραντεβού και μπορεί να βάλει σημείωση.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Το ραντεβού ολοκληρώθηκε"),
            @ApiResponse(responseCode = "401", description = "Δεν είναι logged in"),
            @ApiResponse(responseCode = "403", description = "Μόνο vet"),
            @ApiResponse(responseCode = "404", description = "Δεν βρέθηκε ραντεβού")
    })
    @PatchMapping("/{id}/complete")
    public AppointmentResponse complete(@AuthenticationPrincipal Person user,
                                        @PathVariable Long id,
                                        @Valid @RequestBody NotesRequest req) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        if (user.getRole() != Role.VET) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only vets can complete");

        Appointment updated = appointmentService.complete(id, user, req.notes());
        return AppointmentMapper.toResponse(updated);
    }

    public record CreateAppointmentRequest(
            @NotNull Long petId,
            @NotNull Long vetId,
            @NotNull LocalDateTime start,
            @NotNull AppointmentReason reason
    ) {}

    public record NotesRequest(
            @NotBlank String notes
    ) {}
}
