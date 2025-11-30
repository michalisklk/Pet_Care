package gr.hua.dit.petcare.core.repository;


import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByOwner(Person owner);


}

