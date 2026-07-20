package com.project.emprendia.entrepreneurship.repository;

import com.project.emprendia.entrepreneurship.domain.EntitySocialLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntitySocialLinkRepository extends JpaRepository<EntitySocialLink, Long> {

    List<EntitySocialLink> findByEntityId(Long entityId);

    void deleteByEntityId(Long entityId);
}
