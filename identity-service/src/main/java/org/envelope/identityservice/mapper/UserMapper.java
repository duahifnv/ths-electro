package org.envelope.identityservice.mapper;

import org.envelope.identityservice.dto.user.RegistrationRequest;
import org.envelope.identityservice.dto.user.UserResponse;
import org.envelope.identityservice.dto.user.UserUpdateRequest;
import org.envelope.identityservice.entity.Role;
import org.envelope.identityservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public abstract class UserMapper {
    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Mapping(target = "password", qualifiedByName = "getEncodedPassword")
    @Mapping(target = "tag", expression = "java(request.username())")
    public abstract User toUser(RegistrationRequest request, Set<Role> roles);
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "avatarId", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    public abstract void mapRequest(@MappingTarget User user, UserUpdateRequest request);
    public abstract UserResponse toResponse(User user);
    public abstract List<UserResponse> toResponses(Page<User> users);
    @Named("getEncodedPassword")
    protected String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
