package com.vmware.user.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;



/**
 * The user-api-gateway application.
 */

@SpringBootApplication
@RefreshScope
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
}
