package com.project.emprendia.user.service.impl;

import com.project.emprendia.user.domain.AppUser;
import com.project.emprendia.user.domain.UserContact;
import com.project.emprendia.user.dto.UserContactRequest;
import com.project.emprendia.user.dto.UserContactResponse;
import com.project.emprendia.user.exception.BadRequestException;
import com.project.emprendia.user.repository.UserContactRepository;
import com.project.emprendia.user.repository.UserRepository;
import com.project.emprendia.user.service.KeycloakAdminService;
import com.project.emprendia.user.service.UserContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserContactServiceImpl implements UserContactService {

    private final UserContactRepository userContactRepository;
    private final UserRepository userRepository;
    private final KeycloakAdminService keycloakAdminService;

    @Override
    public List<UserContactResponse> findAll() {
        return userContactRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserContactResponse findById(Long id) {
        return userContactRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("User contact not found"));
    }

    @Override
    public List<UserContactResponse> findByUserId(Long userId) {
        return userContactRepository.findByUser_UserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserContactResponse create(UserContactRequest request) {
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            throw new BadRequestException("Cannot create a primary contact directly. Use the email update endpoint.");
        }
        UserContact contact = toEntity(request);
        return toResponse(userContactRepository.save(contact));
    }

    @Override
    @Transactional
    public UserContactResponse update(Long id, UserContactRequest request) {
        UserContact contact = userContactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User contact not found"));

        updateEntity(contact, request);

        if (Boolean.TRUE.equals(contact.getIsPrimary()) && request.getContactValue() != null) {
            AppUser user = contact.getUser();
            keycloakAdminService.updateUser(
                    user.getKeycloakId(),
                    user.getFirstName(),
                    user.getLastName(),
                    request.getContactValue()
            );
            log.info("Email sincronizado con Keycloak para usuario ID: {}", user.getUserId());
        }

        return toResponse(userContactRepository.save(contact));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userContactRepository.deleteById(id);
    }

    private UserContactResponse toResponse(UserContact contact) {
        return UserContactResponse.builder()
                .userContactId(contact.getUserContactId())
                .userId(contact.getUser().getUserId())
                .contactTypeId(contact.getContactTypeId())
                .contactValue(contact.getContactValue())
                .isPrimary(contact.getIsPrimary())
                .build();
    }

    private UserContact toEntity(UserContactRequest request) {
        AppUser user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
        
        UserContact contact = new UserContact();
        contact.setUser(user);
        contact.setContactTypeId(request.getContactTypeId());
        contact.setContactValue(request.getContactValue());
        contact.setIsPrimary(request.getIsPrimary());
        return contact;
    }

    private void updateEntity(UserContact contact, UserContactRequest request) {
        contact.setContactTypeId(request.getContactTypeId());
        contact.setContactValue(request.getContactValue());
        contact.setIsPrimary(request.getIsPrimary());
    }
}
