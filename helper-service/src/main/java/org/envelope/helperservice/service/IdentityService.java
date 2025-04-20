package org.envelope.helperservice.service;

import org.envelope.helperservice.dto.Role;
import org.envelope.helperservice.dto.RoleDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IdentityService {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${client.identity-service.url}")
    private String identityServiceUrl;
    @Value("${client.identity-service.context-path}")
    private String identityServiceContextPath;

    public Set<Role> getClientRoles(String token) throws Exception {
        Set<RoleDto> clientRolesDto = getClientRolesDto(token);

        return clientRolesDto.stream()
                .map(dtoName -> getRole(dtoName.roleName()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }
    private Set<RoleDto> getClientRolesDto(String token) throws Exception {
        try {
            String url = identityServiceUrl + identityServiceContextPath + "/users/me/roles";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            ResponseEntity<Set<RoleDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new Exception();
        }
    }
    public Optional<Role> getRole(String name) {
        try {
            return Optional.of(Role.valueOf(name.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
