package com.project.emprendia.entrepreneurship.repository;

import com.project.emprendia.entrepreneurship.domain.EntrepreneurshipPortal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntrepreneurshipPortalRepository extends JpaRepository<EntrepreneurshipPortal, Long> {

    Optional<EntrepreneurshipPortal> findByEntrepreneurshipEntrepreneurshipId(Long entrepreneurshipId);

    Optional<EntrepreneurshipPortal> findBySubdomain(String subdomain);

    boolean existsBySubdomain(String subdomain);

    void deleteByEntrepreneurshipEntrepreneurshipId(Long entrepreneurshipId);
}
