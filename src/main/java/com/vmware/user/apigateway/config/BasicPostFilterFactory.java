//package com.vmware.user.apigateway.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.ObjectProvider;
//import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.*;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.reactive.DispatcherHandler;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.net.URI;
//
//import static java.util.Optional.ofNullable;
//import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;
//
//@Component
//@Slf4j
//public class BasicPostFilterFactory extends AbstractGatewayFilterFactory<BasicPostFilterFactory.Config> {
//
//	private final ObjectProvider<DispatcherHandler> dispatcherHandlerProvider;
//
//	private volatile DispatcherHandler dispatcherHandler;
//
//	public BasicPostFilterFactory(ObjectProvider<DispatcherHandler> dispatcherHandlerProvider) {
//		super(Config.class);
//		this.dispatcherHandlerProvider = dispatcherHandlerProvider;
//	}
//
//	public static class Config {
//		private String forwardUrl;
//		private String healthCheckUrl;
//		private String basicAuth;
//
//		public Config() {
//		}
//
//		public String getForwardUrl() {
//			return forwardUrl;
//		}
//
//		public void setForwardUrl(String forwardUrl) {
//			this.forwardUrl = forwardUrl;
//		}
//
//		public String getHealthCheckUrl() {
//			return healthCheckUrl;
//		}
//
//		public void setHealthCheckUrl(String healthCheckUrl) {
//			this.healthCheckUrl = healthCheckUrl;
//		}
//
//		public String getBasicAuth() {
//			return basicAuth;
//		}
//
//		public void setBasicAuth(String basicAuth) {
//			this.basicAuth = basicAuth;
//		}
//	}
//
//	private DispatcherHandler getDispatcherHandler() {
//		if (dispatcherHandler == null) {
//			dispatcherHandler = dispatcherHandlerProvider.getIfAvailable();
//		}
//
//		return dispatcherHandler;
//	}
//
//	@Override
//	public GatewayFilter apply(Config config) {
//		return (exchange, chain) -> {
//
//			String healthCheckUrl = config.getHealthCheckUrl();
//			RestTemplate restTemplate = new RestTemplate();
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType(MediaType.APPLICATION_JSON);
//			headers.add("Authorization", config.getBasicAuth());
//			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
//			try{
//				ResponseEntity<String> response = restTemplate.exchange(healthCheckUrl, HttpMethod.GET, entity, String.class);
//			} catch (RestClientException e) {
//				URI uri = exchange.getRequest().getURI();
//				boolean encoded = containsEncodedParts(uri);
//				URI requestUrl = UriComponentsBuilder.fromUri(uri).host(null).port(null)
//						.uri(URI.create(config.getForwardUrl()+exchange.getRequest().getPath())).scheme(null).build(encoded).toUri();
//				exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
//				reset(exchange);
//				ServerHttpRequest request = exchange.getRequest().mutate().uri(requestUrl).build();
//				return getDispatcherHandler().handle(exchange.mutate().request(request).build());
//			}
//			ServerHttpRequest mutatedReq = exchange.getRequest().mutate().build();
//			return chain.filter(exchange.mutate().request(mutatedReq).build());
//		};
//	}
//
//	private void addExceptionDetails(Throwable t, ServerWebExchange exchange) {
//		ofNullable(t).ifPresent(
//				exception -> exchange.getAttributes().put(CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR, exception));
//	}
//
//}