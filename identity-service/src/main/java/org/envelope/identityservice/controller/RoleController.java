package org.envelope.identityservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.envelope.identityservice.dto.role.RoleDto;
import org.envelope.identityservice.mapper.RoleMapper;
import org.envelope.identityservice.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить список всех ролей")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Set<RoleDto> findAllRoles(@RequestParam(defaultValue = "0") @Min(0) Integer pageNumber,
                                     @RequestParam(defaultValue = "5") @Min(3) Integer pageSize) {
        return roleMapper.toDtos(roleService.findAllRoles(pageNumber, pageSize));
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить роль по id")
    @GetMapping("/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoleDto findRoleById(@PathVariable Integer id) {
        return roleMapper.toDto(roleService.findById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить роль по названию")
    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public RoleDto findRoleByName(@PathVariable String name) {
        return roleMapper.toDto(roleService.findByName(name));
    }
}
