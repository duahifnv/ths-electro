package org.envelope.identityservice.service;

import lombok.RequiredArgsConstructor;
import org.envelope.identityservice.dto.role.RoleDto;
import org.envelope.identityservice.dto.role.RoleNamesDto;
import org.envelope.identityservice.entity.Role;
import org.envelope.identityservice.entity.User;
import org.envelope.identityservice.exception.ResourceNotFoundException;
import org.envelope.identityservice.mapper.RoleMapper;
import org.envelope.identityservice.repository.RoleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public Set<Role> findAllRoles() {
        return new HashSet<>(roleRepository.findAll());
    }
    public Set<Role> findAllRoles(Integer pageNumber, Integer pageSize) {
        return roleRepository.findAll(getPageRequest(pageNumber, pageSize)).toSet();
    }
    public Set<Role> findAllRoles(RoleNamesDto roleNamesDto) {
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNamesDto.roles()) {
            roles.add(findByName(roleName));
        }
        return roles;
    }
    public Role findById(Integer id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Роль с id %s не найдена".formatted(id)));
    }
    public Role findByName(String name) {
        return roleRepository.findByName(roleMapper.toFormattedName(name))
                .orElseThrow(() -> new ResourceNotFoundException("Роль %s не найдена".formatted(name)));
    }
    public void validateUserRoles(User user, Set<Role> roles) {
        Role adminRole = findByName("admin");
        if (user.getRoles().contains(adminRole) != roles.contains(adminRole)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя выдать/удалить роль администратора");
        }
    }
    private PageRequest getPageRequest(Integer pageNumber, Integer pageSize) {
        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "name"));
    }
}
