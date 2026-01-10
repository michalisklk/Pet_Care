package gr.hua.dit.petcare.core.repository;


import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository για το Person entity.
 * Χρησιμοποιείται από το service layer και το Spring Security (login μέσω email)
 */
public interface UserRepository extends JpaRepository<Person, Long>{


    /**
     * Εύρεση χρήστη με το ID.
     */
    Optional<Person> findById(Long id);

    /**
     * Εύρεση χρήστη με το email (για το login).
     * Χρησιμοποιείται από το Spring Security για το authentication.
     */
    Optional<Person> findByEmail(String email);

    List<Person> findByRole(Role role);

}
