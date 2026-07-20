package com.project.emprendia.entrepreneurship.repository;

import com.project.emprendia.entrepreneurship.domain.EntityPortal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntityPortalRepository extends JpaRepository<EntityPortal, Long> {

    Optional<EntityPortal> findByEntityId(Long entityId);

    Optional<EntityPortal> findBySubdomain(String subdomain);

    boolean existsBySubdomain(String subdomain);

    void deleteByEntityId(Long entityId);
}
