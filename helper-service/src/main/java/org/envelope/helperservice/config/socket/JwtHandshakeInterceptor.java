package org.envelope.helperservice.config.socket;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.dto.Role;
import org.envelope.helperservice.dto.UserAgent;
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
import java.util.Optional;
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
        String token = extractToken(authorizationHeader, (ServletServerHttpRequest) request);
        UserAgent userAgent = extractAgent((ServletServerHttpRequest) request);
        String username = extractUsername((ServletServerHttpRequest) request);
        try {
            Set<Role> roles = identityService.getClientRoles(token);
            if (roles.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Отсутствуют требуемые роли");
            }
            Role agentRole = getAgentRole(roles, userAgent);
            attributes.put("role", agentRole);
            attributes.put("username", username);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Невалидный JWT токен");
        }
        return true;
    }
    private Role getAgentRole(Set<Role> userRoles, UserAgent userAgent) {
        if (userRoles.contains(Role.HELPER)) {
            return userAgent == UserAgent.BROWSER_WS ? Role.USER : Role.HELPER;
        }
        else {
            if (userAgent == UserAgent.PYTHON_WS) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Невалидный JWT токен");
            }
            return Role.USER;
        }
    }
    private String extractToken(String authorizationHeader, ServletServerHttpRequest servletRequest) {
        String token;
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            token = servletRequest.getServletRequest().getParameter("token");
            if (token == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Отсутствует JWT токен");
            }
        }
        else token = authorizationHeader.substring(7);
        return token;
    }
    private String extractUsername(ServletServerHttpRequest request) {
        return Optional.ofNullable(request.getServletRequest().getParameter("username"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Отсутствует ID пользователя"));
    }
    private UserAgent extractAgent(ServletServerHttpRequest request) {
        try {
            String userAgentHeader = request.getHeaders().getFirst("user-agent");
            if (userAgentHeader == null) {
                throw new IllegalArgumentException();
            }
            if (userAgentHeader.contains("Mozilla")) {
                return UserAgent.BROWSER_WS;
            }
            return UserAgent.valueOf(userAgentHeader.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неизвестный клиент");
        }
    }
    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                @NonNull WebSocketHandler wsHandler, Exception exception){
    }
}