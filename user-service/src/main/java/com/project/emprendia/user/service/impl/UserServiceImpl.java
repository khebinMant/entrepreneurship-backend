package com.project.emprendia.user.service.impl;

import com.project.emprendia.user.client.SharedServiceClient;
import com.project.emprendia.user.domain.AppUser;
import com.project.emprendia.user.domain.UserContact;
import com.project.emprendia.user.dto.CatalogueValueResponse;
import com.project.emprendia.user.dto.ChangePasswordRequest;
import com.project.emprendia.user.dto.UserRequest;
import com.project.emprendia.user.dto.UserResponse;
import com.project.emprendia.user.dto.UserUpdateRequest;
import com.project.emprendia.user.exception.DuplicateResourceException;
import com.project.emprendia.user.exception.ResourceNotFoundException;
import com.project.emprendia.user.mapping.mapper.UserMapper;
import com.project.emprendia.user.repository.UserContactRepository;
import com.project.emprendia.user.repository.UserRepository;
import com.project.emprendia.user.service.KeycloakAdminService;
import com.project.emprendia.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SharedServiceClient sharedServiceClient;
    private final KeycloakAdminService keycloakAdminService;
    private final UserContactRepository userContactRepository;

    @Override
    public List<UserResponse> findAll() {
        List<UserResponse> users = userRepository.findAll().stream()
            .map(userMapper::toResponse)
            .toList();
        
        users.forEach(this::enrichWithProfileImage);
        return users;
    }

    @Override
    public UserResponse findById(Long id) {
        AppUser user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
        UserResponse response = userMapper.toResponse(user);
        enrichWithProfileImage(response);
        return response;
    }

    @Override
    public UserResponse findByKeycloakId(String keycloakId) {
        AppUser user = userRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new ResourceNotFoundException("User with keycloakId: " + keycloakId));
        UserResponse response = userMapper.toResponse(user);
        enrichWithProfileImage(response);
        return response;
    }

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        String keycloakId = keycloakAdminService.createUser(request);

        AppUser entity = userMapper.toEntity(request);
        entity.setKeycloakId(keycloakId);

        AppUser savedUser = userRepository.save(entity);

        Long emailContactTypeId = getEmailContactTypeId();
        UserContact primaryContact = UserContact.builder()
                .user(savedUser)
                .contactTypeId(emailContactTypeId)
                .contactValue(request.getEmail())
                .isPrimary(true)
                .build();
        userContactRepository.save(primaryContact);

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        AppUser entity = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));

        userMapper.updateEntityFromUpdateRequest(request, entity);

        if (request.getEmail() != null) {
            keycloakAdminService.updateUser(
                entity.getKeycloakId(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail()
            );

            UserContact primaryContact = userContactRepository
                    .findByUser_UserIdAndIsPrimaryTrue(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Primary contact not found for user " + id));
            primaryContact.setContactValue(request.getEmail());
            userContactRepository.save(primaryContact);
        }

        return userMapper.toResponse(userRepository.save(entity));
    }

    @Override
    @Transactional
    public UserResponse updateEmail(Long id, String newEmail) {
        AppUser entity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        keycloakAdminService.updateUser(
                entity.getKeycloakId(),
                entity.getFirstName(),
                entity.getLastName(),
                newEmail
        );

        UserContact primaryContact = userContactRepository
                .findByUser_UserIdAndIsPrimaryTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Primary contact not found for user " + id));
        primaryContact.setContactValue(newEmail);
        userContactRepository.save(primaryContact);

        UserResponse response = userMapper.toResponse(entity);
        enrichWithProfileImage(response);
        return response;
    }

    private Long getEmailContactTypeId() {
        List<CatalogueValueResponse> contactTypes = sharedServiceClient.getValuesByType("CONTACT_TYPE");
        return contactTypes.stream()
                .filter(ct -> "EMAIL".equals(ct.getCode()))
                .findFirst()
                .map(CatalogueValueResponse::getCatalogueValueId)
                .orElseThrow(() -> new RuntimeException("EMAIL contact type not found in catalogue CONTACT_TYPE"));
    }

    @Override
    public String getEmail(Long id) {
        AppUser entity = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return keycloakAdminService.getUserEmail(entity.getKeycloakId());
    }

    @Override
    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        AppUser entity = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));

        keycloakAdminService.changePassword(entity.getKeycloakId(), request.getNewPassword());
        log.info("Contraseña cambiada exitosamente para usuario ID: {}", id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Enriquecer el response con la imagen de perfil del usuario (displayOrder = 0)
     */
    private void enrichWithProfileImage(UserResponse response) {
        try {
            List<Map<String, Object>> images = sharedServiceClient.getImagesForEntity(
                "USER", 
                response.getUserId()
            );
            
            // Buscar la imagen con displayOrder = 0 (foto de perfil principal)
            images.stream()
                .filter(img -> {
                    Object displayOrder = img.get("displayOrder");
                    return displayOrder != null && 
                           (displayOrder instanceof Integer && (Integer) displayOrder == 0);
                })
                .findFirst()
                .ifPresent(profileImage -> {
                    response.setImageUrl((String) profileImage.get("imageUrl"));
                    Object imageId = profileImage.get("imageId");
                    if (imageId instanceof Number) {
                        response.setImageId(((Number) imageId).longValue());
                    }
                });
            
        } catch (Exception e) {
            log.warn("Error al obtener imagen para usuario {}: {}", 
                response.getUserId(), e.getMessage());
            // No fallar si no se puede obtener la imagen
        }
    }
}
