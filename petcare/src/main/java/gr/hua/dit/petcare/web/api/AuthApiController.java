package gr.hua.dit.petcare.web.api;

import gr.hua.dit.petcare.core.dto.UserRegistrationDto;
import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.service.UserService;
import gr.hua.dit.petcare.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    private final UserService userService;


    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthApiController(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }


    // Login: ελέγχει email/password και επιστρέφει JWT
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {

        // Έλεγχος στοιχείων χρήστη (αν είναι σωστά, γίνεται authenticate)
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        Object principal = auth.getPrincipal();

        // Στο project μας περιμένουμε ότι το principal θα είναι Person (επειδή Person implements UserDetails).
        // Αν για κάποιο λόγο ΔΕΝ είναι Person, τότε κάτι έχει στηθεί λάθος στο Spring Security setup
        // (π.χ. άλλαξε το UserDetailsService ή επέστρεψε άλλο UserDetails).
        if (!(principal instanceof Person user)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected principal type");
        }

        // Αν όλα είναι ΟΚ, φτιάχνουμε JWT token με βάση τον Person (email κλπ)
        String token = jwtService.generateToken(user);

        // Επιστρέφουμε το token στον client για να το χρησιμοποιεί στα protected endpoints
        return new TokenResponse(token);


    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public TokenResponse register(@Valid @RequestBody UserRegistrationDto dto) {

        Person user = userService.registerUser(dto);
        String token = jwtService.generateToken(user);
        return new TokenResponse(token);
    }


    public record LoginRequest(
            @NotBlank String email,
            @NotBlank String password
    ) {}

    public record TokenResponse(String token) {}
}
