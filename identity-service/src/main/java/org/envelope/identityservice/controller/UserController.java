package org.envelope.identityservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.envelope.identityservice.dto.role.RoleDto;
import org.envelope.identityservice.dto.role.RoleNamesDto;
import org.envelope.identityservice.dto.user.PasswordUpdateRequest;
import org.envelope.identityservice.dto.user.UserResponse;
import org.envelope.identityservice.dto.user.UserUpdateRequest;
import org.envelope.identityservice.mapper.RoleMapper;
import org.envelope.identityservice.mapper.UserMapper;
import org.envelope.identityservice.service.AvatarService;
import org.envelope.identityservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AvatarService avatarService;
    private final RoleMapper roleMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Получить список всех пользователей")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUsers(@RequestParam(defaultValue = "0") @Min(0) Integer page) {
        return userMapper.toResponses(
                userService.findAllUsers(page)
        );
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Получить пользователя по id")
    @GetMapping("/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserById(@PathVariable Long id) {
        return userMapper.toResponse(userService.findById(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Получить пользователя по username")
    @GetMapping("/username/{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserByUsername(@PathVariable String username) {
        return userMapper.toResponse(userService.findByUsername(username));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Получить пользователя по email")
    @GetMapping("/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserByEmail(@PathVariable String email) {
        return userMapper.toResponse(userService.findByEmail(email));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Получить информацию о ролях пользователя")
    @GetMapping("/username/{username}/roles")
    @ResponseStatus(HttpStatus.OK)
    public Set<RoleDto> getUserRoles(@PathVariable String username) {
        return roleMapper.toDtos(
                userService.findAllRolesByUsername(username)
        );
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Изменить роли пользователя")
    @PutMapping("/username/{username}/roles")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserRoles(@PathVariable String username,
                                @Valid @RequestBody RoleNamesDto roles) {
        userService.updateUserRoles(username, roles);
    }

    // todo: Добавить аналогичный контроллер с ограниченным дто
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить пользователя по tag")
    @GetMapping("/tag/{tag}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserByTag(@PathVariable String tag) {
        return userMapper.toResponse(userService.findByTag(tag));
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить информацию о себе (из контекста)")
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserByContext(Principal principal) {
        return userMapper.toResponse(
                userService.findByUsername(principal.getName())
        );
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить информацию о своих ролях")
    @GetMapping("/me/roles")
    @ResponseStatus(HttpStatus.OK)
    public Set<RoleDto> getUserRolesByContext(Principal principal) {
        return roleMapper.toDtos(
                userService.findAllRolesByUsername(principal.getName())
        );
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Изменить данные о себе")
    @PutMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserByContext(@Valid @RequestBody UserUpdateRequest request, Principal principal) {
        userService.updateUser(principal.getName(), request);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Изменить свой пароль")
    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserPassword(@Valid @RequestBody PasswordUpdateRequest passwordRequest, Principal principal) {
        userService.updateUserPassword(principal.getName(), passwordRequest);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Обновить свою аватарку")
    @PutMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateUserAvatar(@RequestPart("imageFile") MultipartFile imageFile, Principal principal) {
        avatarService.updateAvatar(imageFile, principal.getName());
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Удалить свою аватарку")
    @DeleteMapping(value = "/me/avatar")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserAvatar(Principal principal) {
        avatarService.deleteAvatar(principal.getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Удалить пользователя по id")
    @DeleteMapping("/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
