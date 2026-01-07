package gr.hua.dit.petcare.security;

import gr.hua.dit.petcare.core.model.Person;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final String issuer;
    private final int expirationMinutes;
    private final SecretKey key;

    public JwtService(
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.expiration-minutes}") int expirationMinutes,
            @Value("${security.jwt.secret}") String secret
    ) {
        this.issuer = issuer;
        this.expirationMinutes = expirationMinutes;

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Δημιουργεί access token JWT
    public String generateToken(Person user) {
        Instant now = Instant.now();
        Instant exp = now.plus(expirationMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .issuer(issuer)
                .subject(user.getEmail())
                .claim("uid", user.getId())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key) // HS256 by default
                .compact();
    }
    // Κάνει validate το JWT και επιστρέφει τα claims ή throws αν είναι άκυρο
    public Claims parseClaims(String token) throws JwtException {

        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
