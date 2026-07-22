package com.project.emprendia.event.repository;

import com.project.emprendia.event.domain.EntityPortal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntityPortalRepository extends JpaRepository<EntityPortal, Long> {

    Optional<EntityPortal> findByEntityId(Long entityId);

    void deleteByEntityId(Long entityId);
}
