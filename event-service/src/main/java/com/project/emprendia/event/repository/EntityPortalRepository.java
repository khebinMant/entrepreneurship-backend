package com.project.emprendia.event.repository;

import com.project.emprendia.event.domain.EntityPortal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntityPortalRepository extends JpaRepository<EntityPortal, Long> {

    Optional<EntityPortal> findFirstByEntityIdOrderByPortalIdDesc(Long entityId);

    Optional<EntityPortal> findBySubdomain(String subdomain);

    void deleteByEntityId(Long entityId);

    boolean existsBySubdomain(String subdomain);
}
