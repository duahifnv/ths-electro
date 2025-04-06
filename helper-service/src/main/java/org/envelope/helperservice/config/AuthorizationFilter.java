package org.envelope.helperservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.envelope.helperservice.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Map;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtService jwtService;
    private final HandlerExceptionResolver resolver;
    @Autowired
    public AuthorizationFilter(JwtService jwtService,
                               @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtService = jwtService;
        this.resolver = resolver;
    }
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = authHeader.substring(BEARER_PREFIX.length());
            var username = jwtService.extractUsername(jwt);
            var roles = jwtService.extractRoles(jwt);

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            username, null,
                            roles.stream().map(SimpleGrantedAuthority::new).toList()
                    )
            );

            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            // Обработка ошибок JWT (например, невалидный токен)
            handleException(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        } catch (ServletException | IOException e) {
            // Обработка других исключений
            handleException(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request");
        }
    }

    private void handleException(HttpServletResponse response, int statusCode, String message) {
        try {
            response.setStatus(statusCode);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of("error", message)));
        } catch (IOException ex) {
            // Логирование ошибки записи в ответ
            ex.printStackTrace();
        }
    }
}
