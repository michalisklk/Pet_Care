package gr.hua.dit.petcare.core.service;

import gr.hua.dit.petcare.core.model.*;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {

    Appointment createAppointment(Long ownerId,
                                  Long petId,
                                  Long vetId,
                                  LocalDateTime start,
                                  AppointmentReason reason);

    List<Appointment> getAppointmentsForOwner(Long ownerId);
    List<Appointment> getAppointmentsForVet(Long vetId);

    List<Appointment> getPendingAppointmentsForVet(Long vetId);
    List<Appointment> getConfirmedAppointmentsForVet(Long vetId);
    List<Appointment> getCancelledAppointmentsForVet(Long vetId);
    List<Appointment> getCompletedAppointmentsForVet(Long vetId);

    Appointment confirm(Long appointmentId, Person vet);
    Appointment cancelAsVet(Long appointmentId, Person vet, String notes);
    Appointment complete(Long appointmentId, Person vet, String notes);
}
