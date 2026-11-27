package stayops_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // =========================================================
    // JWT SECRET KEY
    // =========================================================
    //
    // IMPORTANT:
    // This key must stay the same while the application is running.
    //
    // Later, we will move this into application.properties
    // or an environment variable.
    //
    // This must be at least 32 characters for HS256.
    //
    // =========================================================

    private static final String SECRET =
            "StayOpsJwtSecretKeyForLocalDevelopment123456";


    private final SecretKey secretKey =
            Keys.hmacShaKeyFor(
                    SECRET.getBytes(StandardCharsets.UTF_8)
            );


    // =========================================================
    // TOKEN EXPIRATION
    // =========================================================

    // Token expires after 30 days
    private final long expirationTime =
            1000L * 60 * 60 * 24 * 30L;


    // =========================================================
    // GENERATE TOKEN
    // =========================================================

    public String generateToken(
            Long userId,
            String email
    ) {

        return Jwts.builder()

                // User email
                .setSubject(email)

                // User ID
                .claim("userId", userId)

                // Token creation time
                .setIssuedAt(new Date())

                // Token expiration
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expirationTime
                        )
                )

                // Sign token
                .signWith(secretKey)

                .compact();
    }


    // =========================================================
    // EXTRACT EMAIL
    // =========================================================

    public String extractEmail(
            String token
    ) {

        return getClaims(token)
                .getSubject();
    }


    // =========================================================
    // EXTRACT USER ID
    // =========================================================

    public Long extractUserId(
            String token
    ) {

        Number userId =
                getClaims(token)
                        .get("userId", Number.class);

        if (userId == null) {
            return null;
        }

        return userId.longValue();
    }


    // =========================================================
    // READ JWT CLAIMS
    // =========================================================

    private Claims getClaims(
            String token
    ) {

        return Jwts.parser()

                // Verify JWT using our fixed secret
                .verifyWith(secretKey)

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }
}