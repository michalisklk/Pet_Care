package gr.hua.dit.petcare.web.ui;

import gr.hua.dit.petcare.core.model.Appointment;
import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Role;
import gr.hua.dit.petcare.core.repository.UserRepository;
import gr.hua.dit.petcare.core.service.AppointmentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class VetAppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    public VetAppointmentController(AppointmentService appointmentService,
                                    UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
    }
    //Vet βλέπει ολα τα ραντεβου
    @GetMapping("/vet/appointments/all")
    public String getAppointmentsForVet(@RequestParam("vetId") Long vetId,Model model) {

        //βρισκει τον vet με το id του απο την βαση
        Person vet=userRepository.findById(vetId)
                .orElseThrow(() -> new EntityNotFoundException("Vet not found"));

        //Ελεγχος αν ο χρηστης ειναι οντως vet(ισωσ λιγο υπερβολικο αλλλα το εχω βαλει και πιο κατω)
        if(vet.getRole()!=Role.VET){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a vet");
        }

        //περνουμε τα απο το service ολα τα ραντεβου του vet
        List<Appointment> all=appointmentService.getAppointmentsForVet(vetId);
        //Στελνουμε στο thymleaf τον vet και την λιστα με τα appointments
        model.addAttribute("vet", vet);
        model.addAttribute("allAppointments", all);

        //επιστρεφη στο vetallappointments.html
        return"vetallappointments";
    }

    //Vet βλέπει τα PENDING ραντεβου
    // π.χ. http://localhost:8080/vet/appointments?vetId=3
    @GetMapping("/vet/appointments")
    public String vetPendingAppointments(@RequestParam("vetId") Long vetId,
                                         Model model) {
        //βρισκει τον vet με το id του απο την βαση
        Person vet = userRepository.findById(vetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vet not found"));
        //Ελεγχος αν ο χρηστης ειναι οντως vet
        if (vet.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a vet");
        }
        //περνουμε τα απο το service τα ραντεβου του vet που ειναι pending
        List<Appointment> pending = appointmentService.getPendingAppointmentsForVet(vetId);

        //Στελνουμε στο thymleaf τον vet και την λιστα με τα pending appointments
        model.addAttribute("vet", vet);
        model.addAttribute("pendingAppointments", pending);
        //επιστρεφη στο vetpendappointments.html
        return "vetpendappointments";
    }

    //Confirm(το ραντεβου επιβεβαιωθηκε και θα γινει κανονικα)
    @PostMapping("/vet/appointments/{id}/confirm")
    public String confirm(@PathVariable("id") Long appointmentId,
                          @RequestParam("vetId") Long vetId) {
        //βρισκει τον vet με το id του απο την βαση
        Person vet = userRepository.findById(vetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vet not found"));

        try {
            //αλλαζει το ραντεβου confirm
            appointmentService.confirm(appointmentId, vet);
        } catch (EntityNotFoundException | IllegalStateException e) {}

        return "redirect:/vet/appointments?vetId=" + vetId;
    }

    //Cancel(το ραντεβου ακυρωθηκε και δεν θα γινει)
    @PostMapping("/vet/appointments/{id}/cancel")
    public String cancel(@PathVariable("id") Long appointmentId,
                         @RequestParam("vetId") Long vetId,
                         @RequestParam(value = "notes", required = false) String notes) {
        //βρισκει τον vet με το id του απο την βαση
        Person vet = userRepository.findById(vetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vet not found"));

        try {
            //αλλαζει το ραντεβου σε cancel
            appointmentService.cancelAsVet(appointmentId, vet, notes);
        } catch (EntityNotFoundException | IllegalStateException e) {
        }

        return "redirect:/vet/appointments?vetId=" + vetId;
    }

    //Complete(το ραντεβου ολοκληρώθηκε επιτυχώς)
    @PostMapping("/vet/appointments/{id}/complete")
    public String complete(@PathVariable("id") Long appointmentId,
                           @RequestParam("vetId") Long vetId,
                           @RequestParam(value = "notes", required = false) String notes) {
        //βρισκει τον vet με το id του απο την βαση
        Person vet = userRepository.findById(vetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vet not found"));

        try {
            appointmentService.complete(appointmentId, vet, notes);//αλλαζει το ραντεβου σε complete
        } catch (EntityNotFoundException | IllegalStateException e) {
        }
        return "redirect:/vet/appointments?vetId=" + vetId;
    }
}

