package com.vmware.user.apigateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * <code>UserDetailService</code> is class to hold authenticated user details.
 */
@Component
public class UserDetailService implements ReactiveUserDetailsService {

    /**
     * The Encoder.
     */
    @Autowired
    PasswordEncoder encoder;
    @Value("${security.user.name}")
    private String username;
    @Value("${security.user.password}")
    private String password;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        User.UserBuilder user = User.withUsername(username)
				.roles("ADMIN")
				.password(encoder.encode(password));
		return  Mono.just(user.build());
    }
}
