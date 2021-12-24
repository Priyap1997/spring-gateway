package com.vmware.user.apigateway;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;


/**
 * The user-api-gateway application.
 */
@EnableRetry
@SpringBootApplication
@RefreshScope
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.vmware"})
public class UserApiGatewayApplication {


    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UserApiGatewayApplication.class, args);
    }


    /**
     * Reactive resilience4j bean to set circuitBreaker and timeLimiter config.
     *
     * @param circuitBreakerRegistry the circuit breaker registry
     * @param timeLimiterRegistry    the time limiter registry
     * @return the reactive resilience4jCircuitBreakerFactory.
     */
    @Bean
    public ReactiveResilience4JCircuitBreakerFactory reactiveResilience4JCircuitBreakerFactory(final CircuitBreakerRegistry circuitBreakerRegistry, final TimeLimiterRegistry timeLimiterRegistry) {
        ReactiveResilience4JCircuitBreakerFactory reactiveResilience4JCircuitBreakerFactory = new ReactiveResilience4JCircuitBreakerFactory();
        reactiveResilience4JCircuitBreakerFactory.configureCircuitBreakerRegistry(circuitBreakerRegistry);
        reactiveResilience4JCircuitBreakerFactory.configureDefault(id -> {
            CircuitBreakerConfig circuitBreakerConfig = circuitBreakerRegistry.find(id).isPresent() ? circuitBreakerRegistry.find(id).get().getCircuitBreakerConfig()
                    : circuitBreakerRegistry.getDefaultConfig();
            TimeLimiterConfig timeLimiterConfig = timeLimiterRegistry.find(id).isPresent() ? timeLimiterRegistry.find(id).get().getTimeLimiterConfig()
                    : timeLimiterRegistry.getDefaultConfig();

            return new Resilience4JConfigBuilder(id)
                    .circuitBreakerConfig(circuitBreakerConfig)
                    .timeLimiterConfig(timeLimiterConfig)
                    .build();
        });
        return reactiveResilience4JCircuitBreakerFactory;
    }
}
