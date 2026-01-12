package gr.hua.dit.petcare.security;

import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    // Εξαιρεί public endpoints από JWT
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/");
    }

    //Κάνει authenticate το request με JWT
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // token είτε από Authorization header είτε από query param t
        String token = null;

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        if (token == null || token.isBlank()) {
            token = request.getParameter("t");
        }

        // Αν δεν υπάρχει token, συνεχίζει κανονικά
        if (token == null || token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtService.parseClaims(token);
            String email = claims.getSubject();

            Person user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {

                var currentAuth = SecurityContextHolder.getContext().getAuthentication();
                boolean sameUser = false;

                if (currentAuth != null && currentAuth.getPrincipal() instanceof Person p) {
                    sameUser = p.getEmail() != null && p.getEmail().equalsIgnoreCase(user.getEmail());
                }

                // Μετατροπή role σε authority: ROLE_VET / ROLE_OWNER
                // αν υπάρχει token, κάνει authenticate ΑΚΟΜΑ ΚΙ ΑΝ υπάρχει ήδη session auth
                if (!sameUser) {
                    String roleName = "ROLE_" + user.getRole().name(); // ROLE_VET / ROLE_PET_OWNER
                    var auth = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            List.of(new SimpleGrantedAuthority(roleName))
                    );
                    //Θέτουμε Authentication στο SecurityContext (από εδώ και πέρα θεωρείται logged-in)
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

        } catch (Exception ex) {
            // άκυρο token -> απλά δεν κάνουμε authenticate (θα φάει 401)
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

}
