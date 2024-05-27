package com.uni.research_portal.util;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Component
public class Jwt {

    private static String SECRET_KEY = "secret";

    private String secret_key; //

    @PostConstruct
    public void init() {
        SECRET_KEY = secret_key;
    }

    private static final long EXPIRATION_TIME = 86400000; // 1 day

    public static String generateToken(String subject) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);
        Map<String, Object> claims = new HashMap<>();
        if(SECRET_KEY== null){
            SECRET_KEY = "testutil";
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


    public static boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
            Date expirationDate = claims.getExpiration();
            Date now = new Date();
            if (expirationDate.after(now)) {
                return true;
            }
        }
        catch (SignatureException | ExpiredJwtException | IllegalArgumentException e) {
        }

        return false;
    }

    public static String extractSubject(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();

            return claims.getSubject();
        }
        catch (Exception e) {
            return null;
        }
    }

}

