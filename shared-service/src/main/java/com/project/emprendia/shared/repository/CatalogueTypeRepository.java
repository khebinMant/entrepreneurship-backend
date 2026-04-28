package com.project.emprendia.shared.repository;

import com.project.emprendia.shared.domain.CatalogueType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatalogueTypeRepository extends JpaRepository<CatalogueType, Long> {
    Optional<CatalogueType> findByCode(String code);
    boolean existsByCode(String code);
}
