package com.vmware.user.apigateway.config;


import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationManager.class);
    String authToken = null;
    String authNmae = null;
	
   
    @Autowired
    UserDetailService userDetailsService;

    @Value("${security.user.name}")
    private String username;

    @Value("${security.user.password}")
    private String password;
    @Override
    @SuppressWarnings("unchecked")
    public Mono<Authentication> authenticate(Authentication authentication) {
    	
    	List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    	authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    	 
    	if(authentication.getCredentials() != null || !authentication.getCredentials().equals("")) {
    		 authToken = authentication.getCredentials().toString();
    	}
    	if(authentication.getPrincipal() != null || !authentication.getPrincipal().equals("")) {
    	 authNmae = authentication.getPrincipal().toString();
        }
        
    	if ( userDetailsService.findByUsername(authNmae) != null && authToken.equals(password) && authNmae.equals(username))
        {
           return Mono.just(new UsernamePasswordAuthenticationToken(username, password ,authorities));
        }
        else if(authToken.isEmpty() && authNmae.equals(null)) {
            return Mono.error(new LoginException("Full authentication is required to access this resource"));
        }
        else {
            logger.info("AuthenticationServiceException");
            return Mono.error(new LoginException("Bad credentials")); 
        }
    }
}
