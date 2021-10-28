package com.vmware.user.apigateway.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.security.auth.login.LoginException;

@Component
public class AuthorizationManager implements ReactiveAuthorizationManager {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationManager.class);

    @Override
    public Mono<AuthorizationDecision> check(Mono authentication, Object object) {
        return authentication
                .map(a -> {
                    return new AuthorizationDecision(true);
                }).switchIfEmpty(Mono.defer(() -> Mono.error(new LoginException("Full authentication is required to access this resource"))));
    }

    @Override
    public Mono<Void> verify(Mono authentication, Object object) {
        logger.info("verify authentication: {}, object: {}",authentication,object);
        return null;
    }
}
