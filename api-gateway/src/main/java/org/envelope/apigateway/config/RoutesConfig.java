package org.envelope.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class RoutesConfig {
    @Value("${hosts.identity-service}")
    private String identityServiceHost;
    @Value("${hosts.image-service}")
    private String imageServiceHost;
    @Bean
    public RouterFunction<ServerResponse> identityServiceRoute() {
        return route("identity-service-route")
                .route(path("/identity/**"), http(identityServiceHost))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> imageServiceRoute() {
        return route("image-service-route")
                .route(path("/images/**"), http(imageServiceHost))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> authenticationServiceRouteSwagger() {
        return route("identity-service-route-swagger")
                .route(path("/aggregate/identity-service/v1/api-docs"), http(identityServiceHost))
                .filter(setPath("/api/identity/api-docs"))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> imageServiceRouteSwagger() {
        return route("image-service-route-swagger")
                .route(path("/aggregate/image-service/v1/api-docs"), http(imageServiceHost))
                .filter(setPath("/api/images/api-docs"))
                .build();
    }
}
