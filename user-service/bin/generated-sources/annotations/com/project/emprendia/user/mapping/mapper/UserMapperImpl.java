package com.project.emprendia.user.mapping.mapper;

import com.project.emprendia.user.domain.AppUser;
import com.project.emprendia.user.dto.UserRequest;
import com.project.emprendia.user.dto.UserResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-30T18:01:41-0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toResponse(AppUser entity) {
        if ( entity == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.createdAt( entity.getCreatedAt() );
        userResponse.firstName( entity.getFirstName() );
        userResponse.keycloakId( entity.getKeycloakId() );
        userResponse.lastName( entity.getLastName() );
        userResponse.profilePictureUrl( entity.getProfilePictureUrl() );
        userResponse.updatedAt( entity.getUpdatedAt() );
        userResponse.userId( entity.getUserId() );

        return userResponse.build();
    }

    @Override
    public AppUser toEntity(UserRequest request) {
        if ( request == null ) {
            return null;
        }

        AppUser.AppUserBuilder appUser = AppUser.builder();

        appUser.firstName( request.getFirstName() );
        appUser.keycloakId( request.getKeycloakId() );
        appUser.lastName( request.getLastName() );
        appUser.profilePictureUrl( request.getProfilePictureUrl() );

        return appUser.build();
    }

    @Override
    public void updateEntityFromRequest(UserRequest request, AppUser entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getFirstName() != null ) {
            entity.setFirstName( request.getFirstName() );
        }
        if ( request.getLastName() != null ) {
            entity.setLastName( request.getLastName() );
        }
        if ( request.getProfilePictureUrl() != null ) {
            entity.setProfilePictureUrl( request.getProfilePictureUrl() );
        }
    }
}
