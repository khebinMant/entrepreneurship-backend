package com.project.emprendia.user.repository;

import com.project.emprendia.user.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByKeycloakId(String keycloakId);
    boolean existsByKeycloakId(String keycloakId);
}
