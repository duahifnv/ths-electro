package org.envelope.helperservice.config.socket;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.Role;
import org.envelope.helperservice.service.IdentityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private static final String BEARER_PREFIX = "Bearer ";
    private final IdentityService identityService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) {
        String authorizationHeader = request.getHeaders().getFirst("Authorization");
        String token;
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            if (request instanceof ServletServerHttpRequest servletRequest) {
                token = servletRequest.getServletRequest().getParameter("token");
            }
            else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Отсутствует JWT токен");
        }
        else token = authorizationHeader.substring(7);
        try {
            Set<Role> roles = identityService.getClientRoles(token);
            if (roles.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Отсутствуют требуемые роли");
            }
            if (roles.contains(Role.HELPER)) {
                attributes.put("role", Role.HELPER);
            }
            else if (roles.contains(Role.USER)) {
                attributes.put("role", Role.USER);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Невалидный JWT токен");
        }
        return true;
    }
    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler, Exception exception) {
    }
}