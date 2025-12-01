package gr.hua.dit.petcare.core.service.impl;

import gr.hua.dit.petcare.core.dto.UserRegistrationDto;

import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Role;

import gr.hua.dit.petcare.core.repository.UserRepository;
import gr.hua.dit.petcare.core.service.UserService;

import jakarta.validation.ValidationException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // κάνουμε inject όσα χρειαζόμαστε
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Person registerUser(UserRegistrationDto dto) {

        // έλεγχος εαν υπάρχει ήδη χρήστης με το ίδιο email
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ValidationException("A user with this email already exists.");
        }

        // Hash του password
        String hashed = passwordEncoder.encode(dto.getPassword());

        // Δημιουργία entity Person
        Person person = new Person(
                dto.getFullName(),
                dto.getEmail(),
                dto.getMobile(),
                hashed,
                Role.PET_OWNER   // default
        );

        // Αποθήκευση στη βάση
        return userRepository.save(person);
    }

    @Override
    public Person findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public Person findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Μέθοδος για την χρήση του Spring Security.
     * Χρησιμοποιούμε email αντί για username.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("No user was found with email: " + email)
                );
    }
}
