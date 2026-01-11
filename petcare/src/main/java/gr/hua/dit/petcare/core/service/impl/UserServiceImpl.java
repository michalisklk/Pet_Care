package gr.hua.dit.petcare.core.service.impl;

import gr.hua.dit.petcare.core.dto.UserRegistrationDto;
import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Role;
import gr.hua.dit.petcare.core.repository.UserRepository;
import gr.hua.dit.petcare.core.service.UserService;
import gr.hua.dit.petcare.notification.port.PhoneNumberValidationPort;
import jakarta.validation.ValidationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PhoneNumberValidationPort phonePort;


    // κάνουμε inject όσα χρειαζόμαστε
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           PhoneNumberValidationPort phonePort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.phonePort = phonePort;
    }


    @Override
    public Person registerUser(UserRegistrationDto dto) {

        // έλεγχος εαν υπάρχει ήδη χρήστης με το ίδιο email
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ValidationException("A user with this email already exists.");
        }

        String mobile = dto.getMobile();
        if (mobile == null) {
            throw new ValidationException("Mobile number is required.");
        }

        // καθάρισμα (ώστε να μην περνάνε κενά/παύλες)
        mobile = mobile.trim().replace(" ", "").replace("-", "");

        // 10 ψηφία + ξεκινάει με 69
        if (!mobile.matches("^69\\d{8}$")) {
            throw new ValidationException("Mobile must be 10 digits and start with 69 (e.g. 6912345678).");
        }

        // check στη βάση
        if (userRepository.existsByMobile(mobile)) {
            throw new ValidationException("A user with this mobile already exists.");
        }

        // validation μέσω notification_service (libphonenumber)
        var validation = phonePort.validate(mobile);
        if (validation == null || !validation.valid() || validation.e164() == null || validation.e164().isBlank()) {
            throw new ValidationException("Invalid mobile number.");
        }

        if (validation.type() == null || !validation.type().equalsIgnoreCase("mobile")) {
            throw new ValidationException("Phone number is not a mobile number.");
        }


        // Hash του password
        String hashed = passwordEncoder.encode(dto.getPassword());

        // Δημιουργία entity Person
        Person person = new Person(
                dto.getFullName(),
                dto.getEmail(),
                mobile,
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

    @Override
    public List<Person> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }


    /**
     * Μέθοδος για τη χρήση του Spring Security.
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

