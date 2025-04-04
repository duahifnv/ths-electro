package org.envelope.imageservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.function.Function;

@Service
@Slf4j(topic = "Сервис управления токенами")
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public Collection<String> extractRoles(String token) {
        Claims claims = getAllClaims(token);
        return claims.get("roles", Collection.class);
    }
    public <T> T extractClaim(String token, Function<Claims, T> extractFunction) {
        Claims claims = getAllClaims(token);
        return extractFunction.apply(claims);
    }
    private Claims getAllClaims(String token) {
        var payload = Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getPayload();
        log.info("Токен {}... прошел валидацию", token.substring(0, 10));
        return payload;
    }
}
