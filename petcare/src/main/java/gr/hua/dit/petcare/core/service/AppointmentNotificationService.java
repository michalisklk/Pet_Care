package gr.hua.dit.petcare.core.service;

import gr.hua.dit.petcare.core.model.Appointment;
import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.notification.port.PhoneNumberValidationPort;
import gr.hua.dit.petcare.notification.port.SmsNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Αυτό το service ενημερώνει τον owner όταν αλλάζει status ένα appointment.
 * Ενημέρωση = 2 τρόποι:
 * 1) Live μήνυμα στο UI (SSE toast)
 * 2) SMS
 Αν αποτύχει η ειδοποίηση, ΔΕΝ πρέπει να χαλάσει το appointment flow.
 */
@Service
public class AppointmentNotificationService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentNotificationService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final SmsNotificationPort smsPort;
    private final PhoneNumberValidationPort phonePort;


    public AppointmentNotificationService(SmsNotificationPort smsPort,
                                          PhoneNumberValidationPort phonePort) {
        this.smsPort = smsPort;
        this.phonePort = phonePort;

    }

    // Αυτές τις 4 μεθόδους τις καλεί το AppointmentService όταν αλλάζει status
    public void onCreated(Appointment a)   { notifyOwnerSafely(a, msgCreated(a)); }
    public void onConfirmed(Appointment a) { notifyOwnerSafely(a, msgConfirmed(a)); }
    public void onCancelled(Appointment a) { notifyOwnerSafely(a, msgCancelled(a)); }
    public void onCompleted(Appointment a) { notifyOwnerSafely(a, msgCompleted(a)); }

    /**
     * - Βρίσκουμε owner
     * - Στέλνουμε UI toast
     * - Προσπαθούμε για SMS
     */
    private void notifyOwnerSafely(Appointment a, String message) {
        if (a == null) return;

        Person owner = a.getOwner();
        if (owner == null || owner.getId() == null) return;

//        //In-app notification (SSE)
//        try {
//            live.push(owner.getId(), message);
//        } catch (Exception e) {
//            log.warn("In-app notification failed (ignored): {}", e.getMessage());
//        }

        //SMS (μόνο αν υπάρχει κινητό και είναι valid)
        try {
            String mobile = owner.getMobile();
            if (isBlank(mobile)) return;

            var validation = phonePort.validate(mobile);
            if (validation == null || !validation.valid() || isBlank(validation.e164())) return;

            smsPort.sendSms(validation.e164(), message);
        } catch (Exception e) {
            log.warn("SMS failed (ignored): {}", e.getMessage());
        }
    }

    // --------- Εδώ σχηματίζονται τα μηνύματα ---------

    private String msgCreated(Appointment a) {
        return "PetCare: Appointment request created for  "
                + petName(a) + " on " + time(a)
                + " with vet " + vetName(a) + "." +
                reasonPart(a) + ".";
    }

    private String msgConfirmed(Appointment a) {
        return "PetCare: The appointment for "
                + petName(a) + " on " + time(a)
                + " has been confirmed." +
                reasonPart(a) + ".";
    }

    private String msgCancelled(Appointment a) {
        return "PetCare: The appointment for "
                + petName(a) + " on " + time(a)
                + " has been cancelled." +
                reasonPart(a) + ".";
    }

    private String msgCompleted(Appointment a) {
        return "PetCare: The appointment for "
                + petName(a) + " on " + time(a)
                + " has been completed." +
                reasonPart(a) + ".";
    }

    private String reasonPart(Appointment a) {
        if (a == null || a.getReason() == null) return "";
        return " | Notes: " + a.getReason();
    }


    // --------- μικρά helpers (για να μην τρώμε nulls) ---------

    private String time(Appointment a) {
        return (a != null && a.getStartTime() != null)
                ? a.getStartTime().format(FMT)
                : "without time";
    }

    private String petName(Appointment a) {
        if (a == null || a.getPet() == null || isBlank(a.getPet().getName())) return "pet";
        return a.getPet().getName();
    }

    private String vetName(Appointment a) {
        if (a == null || a.getVet() == null) return "vet";
        // Αν δεν έχεις getFullName(), βάλε getName()
        return a.getVet().getFullName();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
