package com.example.Util;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.Details.CustomUserDetailService;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	JwtUtil jwtutil;
	
	@Autowired
	CustomUserDetailService customuserdetailservice;
	
	String token ;
	String username = null;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String requestTokenHeader = request.getHeader("Authorization");
		
		if(requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			token = requestTokenHeader.substring(7);
			JwtResponse jwtresponse = new JwtResponse(token);
			String restoken = jwtresponse.getToken();
			System.out.println(restoken);
			try{
				username = jwtutil.getUsernameFromToken(token);
			}
		    catch (IllegalArgumentException e) {
			System.out.println("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
			System.out.println("JWT Token has expired");
			}
		}
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userdetail = customuserdetailservice.loadUserByUsername(username);
			if(jwtutil.validateToken(token,userdetail)) {
				UsernamePasswordAuthenticationToken usernamepwdauthtoken = new UsernamePasswordAuthenticationToken(userdetail,null,userdetail.getAuthorities());
				usernamepwdauthtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				 SecurityContextHolder.getContext().setAuthentication(usernamepwdauthtoken);
			}
		}
		filterChain.doFilter(request, response);
	}

}
