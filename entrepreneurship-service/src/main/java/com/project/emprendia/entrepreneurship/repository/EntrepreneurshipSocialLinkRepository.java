package com.project.emprendia.entrepreneurship.repository;

import com.project.emprendia.entrepreneurship.domain.EntrepreneurshipSocialLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntrepreneurshipSocialLinkRepository extends JpaRepository<EntrepreneurshipSocialLink, Long> {

    List<EntrepreneurshipSocialLink> findByEntrepreneurshipEntrepreneurshipId(Long entrepreneurshipId);

    void deleteByEntrepreneurshipEntrepreneurshipId(Long entrepreneurshipId);
}
