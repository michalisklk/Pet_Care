package gr.hua.dit.petcare.core.service;

import gr.hua.dit.petcare.core.model.Appointment;

public interface AppointmentNotificationService {

    void onCreated(Appointment a);
    void onConfirmed(Appointment a);
    void onCancelled(Appointment a);
    void onCompleted(Appointment a);
}
