package com.vmware.user.apigateway.config;


import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.GatewayToStringStyler;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.cloud.gateway.support.HttpStatusHolder;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

@Component

public class CircuitBreakerCustomizedGatewayFilterfactory extends AbstractGatewayFilterFactory<CircuitBreakerCustomizedGatewayFilterfactory.Config> {
    public static final String NAME = "CircuitBreakerCustomized";
    private ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;
    private ReactiveCircuitBreaker cb;
    private final ObjectProvider<DispatcherHandler> dispatcherHandlerProvider;
    private volatile DispatcherHandler dispatcherHandler;

    public CircuitBreakerCustomizedGatewayFilterfactory(ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory, ObjectProvider<DispatcherHandler> dispatcherHandlerProvider) {
        super(Config.class);
        this.reactiveCircuitBreakerFactory = reactiveCircuitBreakerFactory;
        this.dispatcherHandlerProvider = dispatcherHandlerProvider;
    }

    @Override
    public String name() {
        return "CircuitBreakerCustomized";
    }

    private DispatcherHandler getDispatcherHandler() {
        if (this.dispatcherHandler == null) {
            this.dispatcherHandler = (DispatcherHandler)this.dispatcherHandlerProvider.getIfAvailable();
        }

        return this.dispatcherHandler;
    }
    private static final String SCHEME_REGEX = "[a-zA-Z]([a-zA-Z]|\\d|\\+|\\.|-)*:.*";
    static final Pattern schemePattern = Pattern.compile(SCHEME_REGEX);

    /* for testing */
    static boolean hasAnotherScheme(URI uri) {
        return schemePattern.matcher(uri.getSchemeSpecificPart()).matches() && uri.getHost() == null
                && uri.getRawPath() == null;
    }


    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("name");
    }

    public GatewayFilter apply(final Config config) {
        final ReactiveCircuitBreaker cb = this.reactiveCircuitBreakerFactory.create(config.getId());
        final Set<HttpStatus> statuses = (Set)config.getStatusCodes().stream().map(HttpStatusHolder::parse).filter((statusHolder) -> {
            return statusHolder.getHttpStatus() != null;
        }).map(HttpStatusHolder::getHttpStatus).collect(Collectors.toSet());
        return new GatewayFilter() {
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
                return cb.run(chain.filter(exchange).doOnSuccess((v) -> {

                    if (statuses.contains(exchange.getResponse().getStatusCode())) {
                        HttpStatus status = exchange.getResponse().getStatusCode();
                        throw new CircuitBreakerStatusCodeException(status);
                    }
                }), (t) -> {
                    if (config.getFallbackUri() == null) {
                        return Mono.error(t);
                    }
                    else {
                        if(config.getHeaderValue()!=null) {
                            Iterator<String> iterate = config.getHeaderValue().iterator();
                            while (iterate.hasNext()) {
                                String split[] = iterate.next().split("=");
                                if((exchange.getRequest().getHeaders().get(split[0]))==null){
                                    exchange.getRequest().mutate().header("x-request-fallback-dc",split[1]).build();
                                    break;
                                }
                                else if(!(exchange.getRequest().getHeaders().get(split[0])).contains(split[1])) {
                                    exchange.getRequest().mutate().header("x-request-fallback-dc",split[1]).build();
                                    break;
                                }else {
                                    return Mono.error(t);
                                }
                            }
                        }
                        exchange.getResponse().setStatusCode((HttpStatus)null);
                        ServerWebExchangeUtils.reset(exchange);
                        URI uri = exchange.getRequest().getURI();
                        boolean encoded = ServerWebExchangeUtils.containsEncodedParts(uri);
                        URI requestUrl = UriComponentsBuilder.fromUri(uri).host((String)null).port((String)null).uri(config.getFallbackUri()).scheme((String)null).build(encoded).toUri();
                        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, requestUrl);
                        addExceptionDetails(t, exchange);
                        ServerWebExchangeUtils.reset(exchange);
                        ServerHttpRequest request = exchange.getRequest().mutate().uri(requestUrl).build();
                        return getDispatcherHandler().handle(exchange.mutate().request(request).build());
                    }
                }).onErrorResume((t) -> {
                    if (java.util.concurrent.TimeoutException.class.isInstance(t)) {
                        return Mono.error(new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, t.getMessage(), t));
                    }
                    if (config.resumeWithoutError) {
                        return Mono.empty();
                    }
                    else {
                        URI uri = exchange.getRequest().getURI();
                        boolean encoded = containsEncodedParts(uri);
                        URI routeUri = route.getUri();
                        if (hasAnotherScheme(routeUri)) {
                            exchange.getAttributes().put(GATEWAY_SCHEME_PREFIX_ATTR, routeUri.getScheme());
                            routeUri = URI.create(routeUri.getSchemeSpecificPart());
                        }
                        URI mergedUrl = UriComponentsBuilder.fromUri(uri)
                                .scheme(routeUri.getScheme()).host(routeUri.getHost()).port(routeUri.getPort()).build(encoded).toUri();
                        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, mergedUrl);
                        return chain.filter(exchange);
                    }
                });
            }

            public String toString() {
                return GatewayToStringStyler.filterToStringCreator(this).append("name", config.getName()).append("fallback", config.fallbackUri).toString();
            }
        };
    }

    private void addExceptionDetails(Throwable t, ServerWebExchange exchange) {
        Optional.ofNullable(t).ifPresent((exception) -> {
            exchange.getAttributes().put(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR, exception);
        });
    }

    public class CircuitBreakerStatusCodeException extends HttpStatusCodeException {
        public CircuitBreakerStatusCodeException(HttpStatus statusCode) {
            super(statusCode);
        }
    }

    public static class Config implements HasRouteId {
        private String name;
        private URI fallbackUri;

        private String routeId;
        private Set<String> statusCodes = new HashSet();

        private LinkedHashSet<String> headerValue = new LinkedHashSet();
        private boolean resumeWithoutError = false;

        public Config() {
        }

        public void setRouteId(String routeId) {
            this.routeId = routeId;
        }

        public String getRouteId() {
            return this.routeId;
        }

        public URI getFallbackUri() {
            return this.fallbackUri;
        }

        public Config setFallbackUri(URI fallbackUri) {
            this.fallbackUri = fallbackUri;
            return this;
        }

        public Config setFallbackUri(String fallbackUri) {
            return this.setFallbackUri(URI.create(fallbackUri));
        }

        public String getName() {
            return this.name;
        }

        public Config setName(String name) {
            this.name = name;
            return this;
        }

        public String getId() {
            return StringUtils.isEmpty(this.name) && !StringUtils.isEmpty(this.routeId) ? this.routeId : this.name;
        }

        public Set<String> getStatusCodes() {
            return this.statusCodes;
        }

        public LinkedHashSet<String> getHeaderValue() {
            return this.headerValue;
        }

        public Config setStatusCodes(Set<String> statusCodes) {
            this.statusCodes = statusCodes;
            return this;
        }

        public Config setHeaderValue(LinkedHashSet<String> headerValue) {
            this.headerValue = headerValue;
            return this;
        }

        public Config addHeaderValue(String headerValue) {
            this.headerValue.add(headerValue);
            return this;
        }

        public Config addStatusCode(String statusCode) {
            this.statusCodes.add(statusCode);
            return this;
        }

        public boolean isResumeWithoutError() {
            return this.resumeWithoutError;
        }

        public void setResumeWithoutError(boolean resumeWithoutError) {
            this.resumeWithoutError = resumeWithoutError;
        }
    }
}
