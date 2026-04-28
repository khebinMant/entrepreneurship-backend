package com.project.emprendia.user.mapping.mapper;

import com.project.emprendia.user.domain.AppUser;
import com.project.emprendia.user.dto.UserRequest;
import com.project.emprendia.user.dto.UserResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(AppUser entity);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "identifications", ignore = true)
    AppUser toEntity(UserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "identifications", ignore = true)
    void updateEntityFromRequest(UserRequest request, @MappingTarget AppUser entity);
}
