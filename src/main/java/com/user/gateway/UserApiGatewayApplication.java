package com.user.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;



/**
 * The user-api-gateway application.
 */

@SpringBootApplication
@ComponentScan(basePackages = {"com.user.gateway"})
public class UserApiGatewayApplication {


    public static void main(String[] args) {
        SpringApplication.run(UserApiGatewayApplication.class, args);
    }
}
