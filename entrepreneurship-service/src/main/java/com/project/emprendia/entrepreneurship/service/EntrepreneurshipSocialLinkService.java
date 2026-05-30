package com.project.emprendia.entrepreneurship.service;

import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipSocialLinkRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipSocialLinkResponse;

import java.util.List;

public interface EntrepreneurshipSocialLinkService {
    List<EntrepreneurshipSocialLinkResponse> findByEntrepreneurshipId(Long entrepreneurshipId);
    EntrepreneurshipSocialLinkResponse findById(Long id);
    EntrepreneurshipSocialLinkResponse create(EntrepreneurshipSocialLinkRequest request);
    EntrepreneurshipSocialLinkResponse update(Long id, EntrepreneurshipSocialLinkRequest request);
    void delete(Long id);
}

