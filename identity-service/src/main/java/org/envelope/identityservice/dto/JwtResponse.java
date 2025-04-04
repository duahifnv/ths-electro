package org.envelope.identityservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record JwtResponse(@Schema(description = "JWT-токен", example = "eyJhbGciOiJIUzI1NiJ9...")
                          String token) {}