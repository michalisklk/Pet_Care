package gr.hua.dit.petcare.config;

import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Pet;
import gr.hua.dit.petcare.core.model.Role;
import gr.hua.dit.petcare.core.repository.UserRepository;
import gr.hua.dit.petcare.core.repository.PetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DevDataConfig {

    @Bean
    public CommandLineRunner initDummyData(UserRepository userRepository,
                                           PetRepository petRepository) {
        return args -> {

            Person owner = new Person(
                    "Dummy Owner1",
                    "owner1@gmail.com",
                    "6944991291",
                    "$2a$10$.bL5STEZQo5QUALltT6mou499V1XtQ9NCRXfxO4XBIhUYabKxv4O2", //demo =password
                    Role.PET_OWNER
            );
            owner = userRepository.save(owner);
            Person owner2 = new Person(
                    "Dummy Owner2",
                    "owner2@gmail.com",
                    "6944991291",
                    "$2a$10$.bL5STEZQo5QUALltT6mou499V1XtQ9NCRXfxO4XBIhUYabKxv4O2", //demo =password
                    Role.PET_OWNER
            );
            owner2 = userRepository.save(owner2);
            Person vet = new Person(
                    "Dummy Vet1",
                    "vet1@gmail.com",
                    "6944991291",
                    "$2a$10$.bL5STEZQo5QUALltT6mou499V1XtQ9NCRXfxO4XBIhUYabKxv4O2",//demo =password
                    Role.VET
            );
            vet = userRepository.save(vet);

            Person vet2 = new Person(
                    "Dummy Vet2",
                    "vet2@gmail.com",
                    "6944991291",
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
