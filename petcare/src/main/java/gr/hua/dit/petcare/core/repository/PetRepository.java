package gr.hua.dit.petcare.core.repository;


import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository για το Pet entity.
 * Χρησιμοποιείται από το PetService για CRUD λειτουργίες
 */
public interface PetRepository extends JpaRepository<Pet, Long> {

    
    /**
     * Επιστρέφει τα ενεργά κατοικίδια με το id του ιδιοκτήτη.
     * (Χρήσιμο όταν έχουμε μόνο το ownerId από το session)
     */
    List<Pet> findByOwnerIdAndActiveTrue(Long ownerId);

}

