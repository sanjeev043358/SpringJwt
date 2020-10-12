package com.example.Util;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class JwtResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public JwtResponse() {
		
	}

	public JwtResponse(String token) {
		super();
		this.token = token;
	}
	
	
}
