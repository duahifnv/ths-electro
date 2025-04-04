package org.envelope.identityservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j(topic = "Сервис управления токенами")
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.duration}")
    private Duration tokenLifetime;
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
        return payload;
    }
    public String generateToken(UserDetails userDetails) {
        // Параметры токена
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesList = userDetails.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).toList();
        claims.put("roles", rolesList);
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + tokenLifetime.toMillis());

        // Собираем токен
        String generatedToken = Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(issuedDate)
                .expiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        log.info("Токен для {} сгенерирован", userDetails);
        return generatedToken;
    }
}
