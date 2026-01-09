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

        String path = request.getRequestURI();
        //Παίρνουμε το Authorization header
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            // Χωρίς Bearer token αφήνουμε το request να συνεχίσει (θα κοπεί από security αν χρειάζεται)
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            Claims claims = jwtService.parseClaims(token);
            String email = claims.getSubject();

            Person user = userRepository.findByEmail(email).orElse(null);
            // Αν ο χρήστης βρέθηκε και δεν έχει γίνει auth, το κάνουμε τώρα
            if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Μετατροπή role σε authority: ROLE_VET / ROLE_OWNER
                String roleName = "ROLE_" + user.getRole().name(); // π.χ. ROLE_VET
                var auth = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(new SimpleGrantedAuthority(roleName))
                );
                //Θέτουμε Authentication στο SecurityContext (από εδώ και πέρα θεωρείται logged-in)
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (Exception ex) {
            // άκυρο token -> απλά δεν κάνουμε authenticate (θα φάει 401)
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
