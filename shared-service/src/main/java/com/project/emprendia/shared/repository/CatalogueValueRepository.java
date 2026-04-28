package com.project.emprendia.shared.repository;

import com.project.emprendia.shared.domain.CatalogueValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogueValueRepository extends JpaRepository<CatalogueValue, Long> {
    boolean existsByCatalogueType_CatalogueTypeIdAndCode(Long typeId, String code);
}
