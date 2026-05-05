package com.project.emprendia.entrepreneurship.repository;

import com.project.emprendia.entrepreneurship.domain.EntrepreneurshipLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntrepreneurshipLocationRepository extends JpaRepository<EntrepreneurshipLocation, Long> {

    Optional<EntrepreneurshipLocation> findByEntrepreneurshipEntrepreneurshipId(Long entrepreneurshipId);

    void deleteByEntrepreneurshipEntrepreneurshipId(Long entrepreneurshipId);
}
