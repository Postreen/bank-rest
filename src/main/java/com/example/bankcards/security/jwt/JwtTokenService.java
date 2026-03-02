package com.example.bankcards.security.jwt;

import com.example.bankcards.entity.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
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
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLE = "role";

    private final JwtProperties props;
    private final SecretKey key;
    private final JwtParser parser;

    public JwtTokenService(JwtProperties props) {
        this.props = props;

        String secret = props.secret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("security.jwt.secret is not set");
        }

        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException("security.jwt.secret must be at least 32 bytes for HS256");
        }

        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.parser = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(props.issuer())
                .build();
    }

    public String generateToken(long userId, String username, Role role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.ttlSeconds());

        return Jwts.builder()
                .issuer(props.issuer())
                .subject(Long.toString(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim(CLAIM_USERNAME, username)
                .claim(CLAIM_ROLE, role.name())
                .signWith(key)
                .compact();
    }

    public Optional<JwtPrincipal> validateAndParse(String token) {
        try {
            Claims claims = parser.parseSignedClaims(token).getPayload();

            long userId = Long.parseLong(claims.getSubject());
            String username = claims.get(CLAIM_USERNAME, String.class);
            String roleStr = claims.get(CLAIM_ROLE, String.class);

            if (username == null || roleStr == null) {
                return Optional.empty();
            }

            Role role = Role.valueOf(roleStr);
            return Optional.of(new JwtPrincipal(userId, username, role));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}