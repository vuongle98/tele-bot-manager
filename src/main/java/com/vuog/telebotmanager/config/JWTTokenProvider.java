package com.vuog.telebotmanager.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JWTTokenProvider {

    @Value("${app.jwt.secret}") // Use the same secret key as in your core service
    private String secretKey;

    @Value("${app.jwt.token-prefix:Bearer }")
    private String tokenPrefix;

    @Value("${app.jwt.header:Authorization}")
    private String tokenHeader;


    public Claims getClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }
}
