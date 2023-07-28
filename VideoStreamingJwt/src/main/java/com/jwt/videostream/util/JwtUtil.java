package com.jwt.videostream.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
	
	@Value("${jwt.secret}")
	private String SECRET_KEY;
	
	@Value("${jwt.tokenvalidity}")
	private int TOKEN_VALIDITY;
	
	long expirationTimeMillis = System.currentTimeMillis() + TOKEN_VALIDITY;
    Date expirationDate = new Date(expirationTimeMillis);

    public String generateToken(UserDetails userDetails) {

		Map<String, Object> claims = new HashMap<>();

		return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expirationTimeMillis))
				.signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();
	}
    
    public Claims verify(String auth) throws Exception {
		try {
			Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(auth).getBody();
			return claims;
		} catch (Exception e) {
			throw new Exception("Access Denied");
		}
	}
    
}
