package org.envelope.identityservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.identityservice.dto.role.RoleNamesDto;
import org.envelope.identityservice.dto.user.PasswordUpdateRequest;
import org.envelope.identityservice.dto.user.RegistrationRequest;
import org.envelope.identityservice.dto.user.UserUpdateRequest;
import org.envelope.identityservice.entity.Role;
import org.envelope.identityservice.entity.User;
import org.envelope.identityservice.exception.UserAlreadyExistsException;
import org.envelope.identityservice.exception.UserNotFoundException;
import org.envelope.identityservice.mapper.UserMapper;
import org.envelope.identityservice.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
@Slf4j(topic = "Сервис пользователей")
@RequiredArgsConstructor
// todo: Сделать enum для ролей
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public Page<User> findAllUsers(Integer page) {
        return userRepository.findAll(getPageRequest(page));
    }
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
    public User findByTag(String tag) {
        return userRepository.findByTag(tag)
                .orElseThrow(() -> new UserNotFoundException(tag));
    }
    public Set<Role> findAllRolesByUsername(String username) {
        return findByUsername(username).getRoles();
    }
    public UserDetailsService getUserDetailsService() {
        return this::findByUsername;
    }
    @Transactional
    public User saveUser(User user) {
        User savedUser = userRepository.save(user);
        log.info("Пользователь {} сохранен в базу данных", savedUser.getUsername());
        return savedUser;
    }
    @Transactional
    public User createUser(RegistrationRequest userRequest, Set<Role> roles) {
        validateUniqueRegister(userRequest);
        User user = userMapper.toUser(userRequest, roles);
        return saveUser(user);
    }
    @Transactional
    public void updateUser(String username, UserUpdateRequest userRequest) {
        User user = findByUsername(username);
        validateUniqueUserUpdate(user, userRequest);
        userMapper.mapRequest(user, userRequest);
        saveUser(user);
    }
    @Transactional
    public void updateUserPassword(String username, PasswordUpdateRequest passwordRequest) {
        User user = findByUsername(username);
        String newPassword = passwordRequest.password();
        String oldPasswordHashed = user.getPassword();
        if (passwordEncoder.matches(newPassword, oldPasswordHashed)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя поменять пароль на существующий");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        saveUser(user);
    }
    @Transactional
    public void updateUserRoles(String username, RoleNamesDto roleNamesDto) {
        Set<Role> updatedRoles = roleService.findAllRoles(roleNamesDto);
        User user = findByUsername(username);
        roleService.validateUserRoles(user, updatedRoles);
        user.setRoles(updatedRoles);
    }
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id.toString());
        }
        userRepository.deleteById(id);
        log.info("Удален пользователь с id: {}", id);
    }
    private void validateUniqueRegister(RegistrationRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("Пользователь с таким логином уже существует");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Пользователь с такой почтой уже существует");
        }
    }
    private void validateUniqueUserUpdate(User contextUser, UserUpdateRequest request) {
        if (userRepository.existsByEmail(request.email())
                && !contextUser.getEmail().equals(request.email())) {
            throw new UserAlreadyExistsException("Пользователь с такой почтой уже существует");
        }
        if (userRepository.existsByTag(request.tag())
                && !contextUser.getTag().equals(request.tag())) {
            throw new UserAlreadyExistsException("Пользователь с таким тегом уже существует");
        }
    }
    private PageRequest getPageRequest(Integer page) {
        return PageRequest.of(page, UserRepository.PAGE_SIZE, Sort.by(Sort.Direction.ASC, "username"));
    }
}