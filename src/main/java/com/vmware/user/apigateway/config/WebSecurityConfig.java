package com.vmware.user.apigateway.config;

//
//import com.vmware.platform.gateway.advice.AppControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {
	private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);


    @Autowired
	AuthenticationManager authenticationManager;

    @Value("${security.user.name}")
	private String username;

	@Value("${security.user.password}")
	private String password;
	@Bean
	public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {

		 return http.csrf().disable()
	               .authorizeExchange()
	                   .matchers(EndpointRequest.toAnyEndpoint()).authenticated()
	                   .anyExchange().authenticated()
	                   .and()
	               .httpBasic().authenticationManager(authenticationManager)
	                  .and()
	               .build();

	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}



}