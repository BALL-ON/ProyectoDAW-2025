package com.ballon.backend.services;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration-ms}")
	private long expirationMs;

	private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

	public String generateToken(String email, boolean remember) {
        // Si se marca "Recordarme", le damos 30 días en milisegundos. Si no usamos el tiempo por defecto
        long tiempoCaducidad = remember ? 1000L * 60 * 60 * 24 * 30 : expirationMs;

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tiempoCaducidad))
                .signWith(getKey())
                .compact();
    }

	public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

	public boolean isTokenValid(String token, UserDetails userDetails) {
		try {
			String email = extractEmail(token);
			return email.equals(userDetails.getUsername()) && !isExpired(token);
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isExpired(String token) {
		return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload().getExpiration()
				.before(new Date());
	}

}

