package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.Details.CustomUserDetailService;
import com.example.Util.JwtAuthenticationEntryPoint;
import com.example.Util.JwtRequestFilter;



@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{


	@Autowired
    CustomUserDetailService customuserdetailservice;	
	
	@Autowired
    JwtRequestFilter jwtrequestfilter;
	
	@Autowired
	JwtAuthenticationEntryPoint entrypoint;
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customuserdetailservice);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		// TODO Auto-generated method stub
		return super.authenticationManager();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	  http.csrf().disable()
	  .authorizeRequests().antMatchers("/api/users/authenticate").permitAll()
	  .antMatchers("/api/users/saveuserdetails").permitAll()
	  .anyRequest().authenticated()
	  .and()
	  .exceptionHandling().authenticationEntryPoint(entrypoint)
	  .and()
	  .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	  
	  http.addFilterBefore(jwtrequestfilter, UsernamePasswordAuthenticationFilter.class);
	}
	
	
	
	
}
