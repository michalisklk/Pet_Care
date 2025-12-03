package gr.hua.dit.petcare.web.ui;

import gr.hua.dit.petcare.core.dto.UserRegistrationDto;
import gr.hua.dit.petcare.core.service.UserService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller για την εγγραφή και login του χρήστη.
 * Περιλαμβάνει: φόρμα εγγραφής (GET /register),
 * αποθήκευση νέου χρήστη (POST /register),
 * εμφάνιση του login page του Spring Security (GET /login)
 */
@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Φόρμα εγγραφής.
     * (GET /register)
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {

        model.addAttribute("user", new UserRegistrationDto()); // κενό DTO στο template
        return "register";
    }

    /**
     * Δέχεται τα δεδομένα της φόρμας,
     * κάνει έλεγχο validation (Email, NotBlank, Size κτλ),
     * αν υπάρχουν σφάλματα επιστρέφουμε ξανά στο register
     * διαφορετικά δημιουργεί τον χρήστη και κάνει redirect στην σελίδα login.
     * (POST /register)
     */
    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("user") UserRegistrationDto dto,
            BindingResult bindingResult,
            Model model
    ) {
        // validation errors
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(dto); // κλήση του service για δημιουργία χρήστη

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage()); // error αν υπάρχει ήδη χρήστης μ αυτο το email
            return "register";
        }

        return "redirect:/login?registered=true"; // redirect στην login
    }

    /**
     * login page που χρησιμοποιεί το Spring Security.
     * (GET /login)
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
