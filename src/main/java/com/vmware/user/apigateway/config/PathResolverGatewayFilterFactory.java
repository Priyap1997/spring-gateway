package com.vmware.user.apigateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchangeDecorator;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

/**
 * <code>PathResolverGatewayFilterFactory</code> is gateway filter to customize request path
 * sent to downstream services.
 */
@Component
public class PathResolverGatewayFilterFactory extends AbstractGatewayFilterFactory<PathResolverGatewayFilterFactory.Config> {

    /**
     * Regexp key.
     */
    public static final String REGEXP_KEY = "regexp";

    /**
     * Replacement key.
     */
    public static final String REPLACEMENT_KEY = "replacement";

    /**
     * Instantiates a new Path resolver gateway filter factory.
     */
    public PathResolverGatewayFilterFactory() {
        super(PathResolverGatewayFilterFactory.Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(REGEXP_KEY, REPLACEMENT_KEY);
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String replacement = config.replacement.replace("$\\", "$");
            URI uri = ((ServerWebExchangeDecorator) exchange).getDelegate().getRequest().getURI();
            ServerHttpRequest req = exchange.getRequest();
            addOriginalRequestUrl(exchange, uri);
            String path = ((ServerWebExchangeDecorator) exchange).getDelegate().getRequest().getPath().toString();
            String requestPath = req.getURI().getRawPath().replace("/**", path);
            String newPath = requestPath.replaceAll(config.regexp, replacement);
            ServerHttpRequest request = req.mutate().path(newPath).build();
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, request.getURI());
            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    /**
     * The type Config.
     */
    public static class Config {
        private String regexp;

        private String replacement;

        /**
         * Gets regexp.
         *
         * @return the regexp
         */
        public String getRegexp() {
            return regexp;
        }

        /**
         * Sets regexp.
         *
         * @param regexp the regexp
         * @return the regexp
         */
        public PathResolverGatewayFilterFactory.Config setRegexp(String regexp) {
            Assert.hasText(regexp, "regexp must have a value");
            this.regexp = regexp;
            return this;
        }

        /**
         * Gets replacement.
         *
         * @return the replacement
         */
        public String getReplacement() {
            return replacement;
        }

        /**
         * Sets replacement.
         *
         * @param replacement the replacement
         * @return the replacement
         */
        public PathResolverGatewayFilterFactory.Config setReplacement(String replacement) {
            Assert.notNull(replacement, "replacement must not be null");
            this.replacement = replacement;
            return this;
        }

    }
}