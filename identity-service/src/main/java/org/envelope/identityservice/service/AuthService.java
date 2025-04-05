package org.envelope.identityservice.service;

import lombok.extern.slf4j.Slf4j;
import org.envelope.identityservice.dto.JwtResponse;
import org.envelope.identityservice.dto.user.CredentialsRequest;
import org.envelope.identityservice.dto.user.RegistrationRequest;
import org.envelope.identityservice.entity.User;
import org.envelope.identityservice.exception.CustomBadCredentialsException;
import org.envelope.identityservice.exception.UserNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j(topic = "Сервис аутентификации")
public record AuthService(JwtService jwtService,
                          UserService userService,
                          AuthenticationManager authenticationManager,
                          RoleService roleService) {
    public JwtResponse registerNewUser(RegistrationRequest userRequest) {
        User user = userService.createUser(userRequest, Set.of(roleService.findByName("user")));
        log.info("Зарегистрирован новый пользователь: {}", user);
        return new JwtResponse(
                jwtService.generateToken(user)
        );
    }
    public JwtResponse authenticate(CredentialsRequest credentialsRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentialsRequest.username(),
                            credentialsRequest.password()
                    )
            );
            log.info("Пользователь {} успешно аутентифицировал себя", credentialsRequest.username());
            User user = userService().findByUsername(credentialsRequest.username());
            return new JwtResponse(
                    jwtService.generateToken(user)
            );
        } catch (BadCredentialsException e) {
            throw new CustomBadCredentialsException();
        } catch (InternalAuthenticationServiceException e) {
            throw new UserNotFoundException();
        }
    }
    public boolean hasAdminRole(Authentication authentication) {
        return getAuthorities(authentication).contains("ROLE_ADMIN");
    }
    public List<String> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}
