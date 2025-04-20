package com.vuog.telebotmanager.common.util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class TokenUtil {

    @Value("${app.jwt.secret}")
    private static String jwtSecret;

    private static final String SECRET_KEY = "your-secret-key";  // Use the same secret key as in the core service

    private static Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public static Claims getClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getUsername(String token) {
        return getClaims(token).getSubject();
    }
}
