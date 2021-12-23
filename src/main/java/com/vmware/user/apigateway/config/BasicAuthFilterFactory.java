package com.vmware.user.apigateway.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * <code>BasicAuthFilterFactory</code> is gateway filter to authenticate downstream services.
 */
@Component
public class BasicAuthFilterFactory extends AbstractGatewayFilterFactory<BasicAuthFilterFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(BasicAuthFilterFactory.class);

    private static String AUTH = "Authorization";

	/**
	 * Instantiates a new Basic auth filter factory.
	 */
	public BasicAuthFilterFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("credentials");
    }

    @Override
    public String name() {
        return "BasicAuth";
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            try {
                String[] configTokens = config.getCredentials().split(":");
                ServerHttpRequest mutatedReq = exchange.getRequest().mutate()
                        .header(AUTH, createBasicAuthHeader(configTokens[0],configTokens[1]))
                        .build();
                return chain.filter(exchange.mutate().request(mutatedReq).build());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

	/**
	 * Create basic auth header string.
	 *
	 * @param username the username
	 * @param password the password
	 * @return the string
	 */
	String createBasicAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        String token = Base64Utils.encodeToString((auth).getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(token);
        return authHeader;
    }

	/**
	 * The type Config.
	 */
	public static class Config {
        private String credentials;

		/**
		 * Instantiates a new Config.
		 */
		public Config() {
        }

		/**
		 * Gets credentials.
		 *
		 * @return the credentials
		 */
		public String getCredentials() {
            return credentials;
        }

		/**
		 * Sets credentials.
		 *
		 * @param credentials the credentials
		 */
		public void setCredentials(String credentials) {
            this.credentials = credentials;
        }

    }

}