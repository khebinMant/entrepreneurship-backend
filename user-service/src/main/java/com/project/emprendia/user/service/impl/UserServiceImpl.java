package com.project.emprendia.user.service.impl;

import com.project.emprendia.user.domain.AppUser;
import com.project.emprendia.user.dto.UserRequest;
import com.project.emprendia.user.dto.UserResponse;
import com.project.emprendia.user.exception.DuplicateResourceException;
import com.project.emprendia.user.exception.ResourceNotFoundException;
import com.project.emprendia.user.mapping.mapper.UserMapper;
import com.project.emprendia.user.repository.UserRepository;
import com.project.emprendia.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
            .map(userMapper::toResponse)
            .toList();
    }

    @Override
    public UserResponse findById(Long id) {
        AppUser user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse findByKeycloakId(String keycloakId) {
        AppUser user = userRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new ResourceNotFoundException("User with keycloakId: " + keycloakId));
        return userMapper.toResponse(user);
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
}
