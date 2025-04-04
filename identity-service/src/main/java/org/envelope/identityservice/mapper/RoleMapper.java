package org.envelope.identityservice.mapper;

import org.envelope.identityservice.dto.role.RoleDto;
import org.envelope.identityservice.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class RoleMapper {
    @Mapping(source = "name", target = "roleName", qualifiedByName = "toRowNameFromString")
    public abstract RoleDto toDto(Role rolesPage);
    public abstract Set<RoleDto> toDtos(Set<Role> roles);

    @Mapping(source = "roleName", target = "name", qualifiedByName = "toFormattedName")
    public abstract Role toRole(String roleName);
    public abstract Set<Role> toRoles(Set<String> roles);

    @Named("toRowNameFromString")
    public String toRawName(String roleName) {
        return roleName.replace("ROLE_", "").toLowerCase();
    }
    @Named("toRawNamesFromRoles")
    public abstract Set<String> toRawNames(Set<Role> roles);

    public String toRawName(Role role) {
        return toRawName(role.getName());
    }
    @Named("toFormattedName")
    public String toFormattedName(String roleName) {
        return roleName.startsWith("ROLE_") ? roleName.toUpperCase() : "ROLE_" + roleName.toUpperCase();
    }
}
