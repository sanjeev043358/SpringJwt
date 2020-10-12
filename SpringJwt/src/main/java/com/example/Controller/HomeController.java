package com.example.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.Details.CustomUserDetailService;
import com.example.Entity.Users;
import com.example.Repositry.UserRepositry;
import com.example.Util.JwtRequest;
import com.example.Util.JwtResponse;
import com.example.Util.JwtUtil;

@RestController
@RequestMapping("/api/users")
public class HomeController {

	@Autowired
    UserRepositry userrepositry;	
	
	@Autowired
	AuthenticationManager authenticationmanager;
	
	@Autowired
	CustomUserDetailService customuserdetailservice;
	
	@Autowired
	JwtUtil jwtutil;
	
	@RequestMapping("/")
	public String getHome() {
		return "Home Controller";
	}
	
	@RequestMapping("getall")
	public List<Users> getAll(){
		return	userrepositry.findAll();
	}
	
	@RequestMapping("getuserlessthanbyage/{age}")
	public List<Users> getAll(@PathVariable int age){
		return	userrepositry.findByage(age);
	}
	
	@RequestMapping("getuserbygender/{gender}")
	public List<Users> getAll(@PathVariable String gender){
		return	userrepositry.findBygender(gender);
	}
	
	@RequestMapping("/saveuserdetails")
	public String saveUsersDetails(@RequestBody Users users) {
		
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		Users userrecord = userrepositry.findTopByOrderByIdDesc();
		
		if(userrecord == null) {
			users.setId(1);
			users.setPassword(bcrypt.encode(users.getPassword()));
			users = userrepositry.save(users);
		}else {
			int ids = userrecord.getId();
			users.setId(ids+1);
			users.setPassword(bcrypt.encode(users.getPassword()));
			users = userrepositry.save(users);
		}
	    int id = users.getId();
	    if(id <= 0) {
	    	return "Not Successfully Inserted";
	    }
		return " SuccessFully Inserted";
	
		
	}
	
	@RequestMapping(value = "/authenticate",method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest jwtrequest) throws Exception{
		authenticate(jwtrequest.getUsername(),jwtrequest.getPassword());
		final UserDetails userdetail = customuserdetailservice.loadUserByUsername(jwtrequest.getUsername());
		final String token = jwtutil.generateToken(userdetail);
		return ResponseEntity.ok(new JwtResponse(token));
	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationmanager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		}catch(DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		}catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}
