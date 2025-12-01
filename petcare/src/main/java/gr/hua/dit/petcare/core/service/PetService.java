package gr.hua.dit.petcare.core.service;

import gr.hua.dit.petcare.core.dto.PetDto;
import gr.hua.dit.petcare.core.model.Pet;

import java.util.List;

/**
 * Service για τις λειτουργίες στα κατοικίδια.
 * Περιλαμβάνει μεθόδους για τον έλεγχο των κατοικίδιων του χρήστη.
 * (δημιουργία κατοικίδιου, λίστα με τα κατοικίδια του ιδιοκτήτη, ενημέρωση στοιχείων κατικοιδίου,
 * εύρεση κατοικίδιου με id και διαγραφή κατοικίδιου.)
 */
public interface PetService {

    /**
     * Δημιουργεί νέο κατοικίδιο.
     */
    Pet createPet(Long ownerId, PetDto dto);

    /**
     * Επιστρέφει όλα τα κατοικίδια του owner
     */
    List<Pet> getPetsForOwner(Long ownerId);

    /**
     * Ενημερώνει τα στοιχεία του κατοικίδιου (έλεγχος ότι ανήκει στον owner).
     */
    Pet updatePet(Long petId, Long ownerId, PetDto dto);

    /**
     * Διαγράφει κατοικίδιου (έλεγχος ότι ανήκει στον owner).
     */
    void deletePet(Long petId, Long ownerId);

    /**
     * Εύρεση κατοικίδιου με την χρήση του id.
     */
    Pet getPetById(Long id);
}
