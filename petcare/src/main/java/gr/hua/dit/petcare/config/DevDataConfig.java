package gr.hua.dit.petcare.config;

import gr.hua.dit.petcare.core.model.*;
import gr.hua.dit.petcare.core.repository.AppointmentRepository;
import gr.hua.dit.petcare.core.repository.UserRepository;
import gr.hua.dit.petcare.core.repository.PetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DevDataConfig {

    @Bean
    public CommandLineRunner initDummyData(UserRepository userRepository,
                                           PetRepository petRepository,
                                           AppointmentRepository appointmentRepository) {
        return args -> {

            Person owner = new Person(
                    "Dummy Owner1",
                    "owner1@gmail.com",
                    "6900000001",
                    "$2a$10$.bL5STEZQo5QUALltT6mou499V1XtQ9NCRXfxO4XBIhUYabKxv4O2", //demo =password
                    Role.PET_OWNER
            );
            owner = userRepository.save(owner);
            Person owner2 = new Person(
                    "Dummy Owner2",
                    "owner2@gmail.com",
                    "6900000002",
                    "$2a$10$.bL5STEZQo5QUALltT6mou499V1XtQ9NCRXfxO4XBIhUYabKxv4O2", //demo =password
                    Role.PET_OWNER
            );
            owner2 = userRepository.save(owner2);
            Person vet = new Person(
                    "Dummy Vet1",
                    "vet1@gmail.com",
                    "6900000003",
                    "$2a$10$.bL5STEZQo5QUALltT6mou499V1XtQ9NCRXfxO4XBIhUYabKxv4O2",//demo =password
                    Role.VET
            );
            vet = userRepository.save(vet);

            Person vet2 = new Person(
                    "Dummy Vet2",
                    "vet2@gmail.com",
                    "6900000004",
                    "$2a$10$.bL5STEZQo5QUALltT6mou499V1XtQ9NCRXfxO4XBIhUYabKxv4O2",//demo =password
                    Role.VET
            );
            vet2 = userRepository.save(vet2);
            Pet pet = new Pet(
                    "Rex",
                    "Dog",
                    "Mixed",
                    3,
                    owner
            );
            pet = petRepository.save(pet);

            Pet pet2 = new Pet(
                    "zizel",
                    "cat",
                    "Mixed",
                    5,
                    owner
            );
            pet2 = petRepository.save(pet2);

            Pet pet3 = new Pet(
                    "Max",
                    "Dog",
                    "Mixed",
                    4,
                    owner2
            );
            pet3 = petRepository.save(pet3);

            // Dummy appointments
            LocalDateTime start1 = LocalDateTime.parse("2026-02-12T12:33:00");
            AppointmentReason reason1 = AppointmentReason.BEHAVIOR_COUNSELING;

            // Δημιουργία appointment
            LocalDateTime end1 = start1.plusMinutes(30);
            Appointment a1 = new Appointment(pet, vet, start1, end1, reason1);
            a1.setStatus(AppointmentStatus.PENDING);
            appointmentRepository.save(a1);


            // Dummy appointments
            LocalDateTime start2 = LocalDateTime.parse("2026-02-13T10:00:00");
            AppointmentReason reason2 = AppointmentReason.GENERAL_CHECKUP;

            // Δημιουργία appointment
            LocalDateTime end2 = start2.plusMinutes(30);
            Appointment a2 = new Appointment(pet2, vet, start2, end2, reason2);
            a2.setStatus(AppointmentStatus.CONFIRMED);
            appointmentRepository.save(a2);


            // Dummy appointments
            LocalDateTime start3 = LocalDateTime.parse("2026-02-14T18:30:00");
            AppointmentReason reason3 = AppointmentReason.DIET;

            // Δημιουργία appointment
            LocalDateTime end3 = start3.plusMinutes(30);
            Appointment a3 = new Appointment(pet3, vet2, start3, end3, reason3);
            a3.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepository.save(a3);


            System.out.println("Owner ID = " + owner.getId());
            System.out.println("Owner2 ID = " + owner2.getId());
            System.out.println("Vet ID   = " + vet.getId());
            System.out.println("Vet2 ID = " + vet2.getId());
            System.out.println("Pet ID   = " + pet.getId());
            System.out.println("Pet2 ID   = " + pet2.getId());
            System.out.println("Pet3 ID   = " + pet3.getId());

        };
    }
}
