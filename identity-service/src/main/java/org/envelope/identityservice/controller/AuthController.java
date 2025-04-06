package org.envelope.identityservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.envelope.identityservice.dto.JwtResponse;
import org.envelope.identityservice.dto.user.CredentialsRequest;
import org.envelope.identityservice.dto.user.RegistrationRequest;
import org.envelope.identityservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {
    private final AuthService authService;
    @Operation(summary = "Зарегистрировать нового пользователя",
            description = "Возвращает сгенерированный JWT-токен")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public JwtResponse registerNewUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        return authService.registerNewUser(registrationRequest);
    }
    @Operation(summary = "Аутентифицировать существующего пользователя",
            description = "Возвращает сгенерированный JWT-токен")
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public JwtResponse authenticateUser(@Valid @RequestBody CredentialsRequest credentialsRequest) {
        return authService.authenticate(credentialsRequest);
    }
}
