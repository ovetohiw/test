package com.phoneshop.security;

import com.phoneshop.user.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private final SecretKey key;
	private final long ttlSeconds;

	public JwtService(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.ttl-seconds}") long ttlSeconds
	) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.ttlSeconds = ttlSeconds;
	}

	public String createToken(Long userId, String email, Role role) {
		Instant now = Instant.now();
		return Jwts.builder()
				.subject(email)
				.claim("uid", userId)
				.claim("role", role.name())
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plusSeconds(ttlSeconds)))
				.signWith(key)
				.compact();
	}

	public Claims parse(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public long getTtlSeconds() {
		return ttlSeconds;
	}
}
