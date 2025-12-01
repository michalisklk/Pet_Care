package gr.hua.dit.petcare.core.service;

import gr.hua.dit.petcare.core.dto.UserRegistrationDto;
import gr.hua.dit.petcare.core.model.Person;

import java.util.Optional;

/**
 * Service για τις λειτουργίες του χρήστη (Person).
 * Περιέχει το business logic για την εγγραφή, εύρεση χρήστη με email (login) και id
 */
public interface UserService {

    /**
     * Εγγραφή νέου χρήστη στο σύστημα,
     * έλεγχο μοναδικότητας email,
     * κάνει hash τον κωδικό και δίνει
     * default Role = PET_OWNER
     */
    Person registerUser(UserRegistrationDto dto);

    /**
     * Εύρεση χρήστη με το email (για το login)
     */
    Person findByEmail(String email);

    /**
     * Εύρεση χρήστη με το id
     */
    Person findById(Long id);
}
