package com.example.bankcards.security.jwt;

import com.example.bankcards.entity.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtTokenService {

    private final JwtProperties props;
    private final SecretKey key;

    public JwtTokenService(JwtProperties props) {
        this.props = props;
        if (props.secret() == null || props.secret().isBlank()) {
            throw new IllegalStateException("JWT_SECRET is not set");
        }
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(long userId, String username, Role role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.ttlSeconds());

        return Jwts.builder()
                .issuer(props.issuer())
                .subject(Long.toString(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("username", username)
                .claim("role", role.name())
                .signWith(key)
                .compact();
    }

    public Optional<JwtPrincipal> validateAndParse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(props.issuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            long userId = Long.parseLong(claims.getSubject());
            String username = claims.get("username", String.class);
            String roleStr = claims.get("role", String.class);

            return Optional.of(new JwtPrincipal(userId, username, Role.valueOf(roleStr)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
