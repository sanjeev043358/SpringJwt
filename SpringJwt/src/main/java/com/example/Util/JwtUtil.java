package com.example.Util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

	private String secret = "secret";

public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
	
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		
	}
	
	private <T> T getClaimsFromToken(String token,Function<Claims,T> claimsresolver){
		final Claims claims = getAllClaimsFromToken(token);
		return claimsresolver.apply(claims);
	}
	//retrieve user from token
	public String getUsernameFromToken(String token) {
		return getClaimsFromToken(token,Claims::getSubject);
	}
	
	//retrieve DatE expiration from token
	private Date getExpirationDateFromToken(String token) {
		return getClaimsFromToken(token,Claims::getExpiration);
	}
	
	//Check if token is expiered 
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
	
	//generting token 
	private String doGenerateToken(Map<String, Object> claims,String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY *1000))
				.signWith(SignatureAlgorithm.HS256, secret).compact();			
				
	}
	
	public String generateToken(UserDetails userdetails) {
		Map<String ,Object> claims = new HashMap<>();
		return doGenerateToken(claims, userdetails.getUsername());
	}
	
	//validate token
	public Boolean validateToken(String  token,UserDetails userdetails) {
		final String  username = getUsernameFromToken(token);
		return (username.equals(userdetails.getUsername()) && !isTokenExpired(token));
	}
	
	
}
