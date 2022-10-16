package com.user.gateway.config;


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

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>AuthenticationManager</code> is to authenticate a user.
 */
@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationManager.class);
    /**
     * The Auth token.
     */
    String authToken = null;
    /**
     * The Auth name.
     */
    String authName = null;


    /**
     * The User details service.
     */
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
        authorities.add(new SimpleGrantedAuthority("ADMIN"));
        if (authentication.getCredentials() != null || !authentication.getCredentials().equals("")) {
            authToken = authentication.getCredentials().toString();
        }
        if (authentication.getPrincipal() != null || !authentication.getPrincipal().equals("")) {
            authName = authentication.getPrincipal().toString();
        }
        if (userDetailsService.findByUsername(authName) != null && authToken.equals(password) && authName.equals(username)) {
            return Mono.just(new UsernamePasswordAuthenticationToken(username, password, authorities));
        } else {
            logger.info("Credentials are not correct for gateway");
            return Mono.error(new LoginException("Bad credentials"));
        }
    }
}
