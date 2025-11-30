package gr.hua.dit.petcare.core.repository;


import gr.hua.dit.petcare.core.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Person, Long>{
    List<Person> findByFullName(String fullName);//ΔΕΝ ΞΕΡΩ ΑΚΟΜ ΑΝ ΧΡΕΙΑΖΕΤΑΙ
    Optional<Person> findById(Long id);
}
