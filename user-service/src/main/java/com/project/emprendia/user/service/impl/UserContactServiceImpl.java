package com.project.emprendia.user.service.impl;

import com.project.emprendia.user.domain.UserContact;
import com.project.emprendia.user.dto.UserContactRequest;
import com.project.emprendia.user.dto.UserContactResponse;
import com.project.emprendia.user.repository.UserContactRepository;
import com.project.emprendia.user.service.UserContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserContactServiceImpl implements UserContactService {

    private final UserContactRepository userContactRepository;

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
    @Transactional
    public UserContactResponse create(UserContactRequest request) {
        UserContact contact = toEntity(request);
        return toResponse(userContactRepository.save(contact));
    }

    @Override
    @Transactional
    public UserContactResponse update(Long id, UserContactRequest request) {
        UserContact contact = userContactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User contact not found"));
        updateEntity(contact, request);
        return toResponse(userContactRepository.save(contact));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userContactRepository.deleteById(id);
    }

    private UserContactResponse toResponse(UserContact contact) {
        UserContactResponse response = new UserContactResponse();
        response.setId(contact.getId());
        response.setContactTypeId(contact.getContactTypeId());
        response.setContactValue(contact.getContactValue());
        response.setIsPrimary(contact.getIsPrimary());
        return response;
    }

    private UserContact toEntity(UserContactRequest request) {
        UserContact contact = new UserContact();
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
