package com.vmware.user.apigateway.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.annotation.RequestScope;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BasicAuthFilterFactory extends AbstractGatewayFilterFactory<BasicAuthFilterFactory.Config> {

	private static String AUTH = "Authorization";

	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList("credentials");
	}

	public BasicAuthFilterFactory() {
		super(Config.class);
	}

	@Override
	public String name() {
		return "BasicAuth";
	}

	public static class Config {
		private String credentials;

		public Config() {
		}

		public String getCredentials() {
			return credentials;
		}

		public void setCredentials(String credentials) {
			this.credentials = credentials;
		}

	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			try {
				String[] configTokens = config.getCredentials().split(":");
				log.info("username  in  basic  auth filter {}",configTokens[0]);
				ServerHttpRequest mutatedReq = exchange.getRequest().mutate().header(AUTH, createBasicAuthHeader(configTokens[0],
						configTokens[1]))
						.build();
				return chain.filter(exchange.mutate().request(mutatedReq).build());

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	String createBasicAuthHeader(String username, String password) {
		String auth = username + ":" + password;
		String token = Base64Utils.encodeToString((auth).getBytes(StandardCharsets.UTF_8));
		String authHeader = "Basic " + new String(token);
		return authHeader;
	}

}