package gr.hua.dit.petcare.web.api;

import gr.hua.dit.petcare.core.model.Person;
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

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthApiController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // Login: ελέγχει email/password και επιστρέφει JWT
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {

        // Έλεγχος στοιχείων χρήστη (αν είναι σωστά, γίνεται authenticate)
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        Object principal = auth.getPrincipal();

        // Αν ο χρήστης είναι Person, φτιάχνουμε token(προαιρετικό TODO )
        if (principal instanceof Person user) {
            String token = jwtService.generateToken(user);
            return new TokenResponse(token);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login succeeded but principal is not Person");
    }

    public record LoginRequest(
            @NotBlank String email,
            @NotBlank String password
    ) {}

    public record TokenResponse(String token) {}
}
