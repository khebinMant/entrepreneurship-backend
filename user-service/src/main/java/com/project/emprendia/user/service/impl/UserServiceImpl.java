package com.project.emprendia.user.service.impl;

import com.project.emprendia.user.client.SharedServiceClient;
import com.project.emprendia.user.domain.AppUser;
import com.project.emprendia.user.dto.UserRequest;
import com.project.emprendia.user.dto.UserResponse;
import com.project.emprendia.user.exception.DuplicateResourceException;
import com.project.emprendia.user.exception.ResourceNotFoundException;
import com.project.emprendia.user.mapping.mapper.UserMapper;
import com.project.emprendia.user.repository.UserRepository;
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
        if (userRepository.existsByKeycloakId(request.getKeycloakId())) {
            throw new DuplicateResourceException("User", "keycloakId", request.getKeycloakId());
        }
        AppUser entity = userMapper.toEntity(request);
        return userMapper.toResponse(userRepository.save(entity));
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        AppUser entity = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
        userMapper.updateEntityFromRequest(request, entity);
        return userMapper.toResponse(userRepository.save(entity));
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
