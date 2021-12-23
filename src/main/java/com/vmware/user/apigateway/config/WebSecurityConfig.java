package com.vmware.user.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * <code>WebSecurityConfig</code> is class to enable Spring Security.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);


	/**
	 * The Authentication manager.
	 */
	@Autowired
    AuthenticationManager authenticationManager;

	/**
	 * The Authorization manager.
	 */
	@Autowired
    AuthorizationManager authorizationManager;

	/**
	 * Security filter chain security web filter chain.
	 *
	 * @param http the http
	 * @return the security web filter chain
	 */
	@Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {

        return http.csrf().disable().authorizeExchange().pathMatchers("/ping").permitAll()
                .pathMatchers("/**").access(authorizationManager)
                .and()
                .httpBasic().and().formLogin().authenticationManager(authenticationManager)
                .and().build();
    }

	/**
	 * Password encoder password encoder.
	 *
	 * @return the password encoder
	 */
	@Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}