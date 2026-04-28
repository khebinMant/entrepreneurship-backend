package com.project.emprendia.user.service.impl;

import com.project.emprendia.user.domain.AppUser;
import com.project.emprendia.user.dto.UserRequest;
import com.project.emprendia.user.dto.UserResponse;
import com.project.emprendia.user.exception.DuplicateResourceException;
import com.project.emprendia.user.exception.ResourceNotFoundException;
import com.project.emprendia.user.mapping.mapper.UserMapper;
import com.project.emprendia.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findAll_shouldReturnMappedList() {
        AppUser user = new AppUser();
        UserResponse response = UserResponse.builder().userId(1L).build();
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        List<UserResponse> result = userService.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void findById_withInvalidId_shouldThrow() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_withDuplicateKeycloak_shouldThrow() {
        UserRequest request = new UserRequest();
        request.setKeycloakId("kc-123");
        when(userRepository.existsByKeycloakId("kc-123")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
            .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void create_withValidRequest_shouldReturnSaved() {
        UserRequest request = new UserRequest();
        request.setKeycloakId("kc-new");
        AppUser entity = new AppUser();
        UserResponse response = UserResponse.builder().keycloakId("kc-new").build();

        when(userRepository.existsByKeycloakId("kc-new")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(entity);
        when(userMapper.toResponse(entity)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertThat(result.getKeycloakId()).isEqualTo("kc-new");
    }
}
