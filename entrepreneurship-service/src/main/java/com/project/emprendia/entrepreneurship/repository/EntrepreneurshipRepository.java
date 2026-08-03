package com.project.emprendia.entrepreneurship.repository;

import com.project.emprendia.entrepreneurship.domain.Entrepreneurship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntrepreneurshipRepository extends JpaRepository<Entrepreneurship, Long> {
    List<Entrepreneurship> findByUserId(Long userId);
    List<Entrepreneurship> findByCategory_CategoryId(Long categoryId);

    long countByUserId(Long userId);

    List<Entrepreneurship> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT e.category.categoryId, e.category.name, COUNT(e) FROM Entrepreneurship e WHERE e.userId = :userId GROUP BY e.category.categoryId, e.category.name ORDER BY COUNT(e) DESC")
    List<Object[]> countByCategoryGroupedByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(e) FROM Entrepreneurship e WHERE e.userId = :userId AND e.isPhysical = true AND e.isDigital = false")
    long countPhysicalOnlyByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(e) FROM Entrepreneurship e WHERE e.userId = :userId AND e.isDigital = true AND e.isPhysical = false")
    long countDigitalOnlyByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(e) FROM Entrepreneurship e WHERE e.userId = :userId AND e.isPhysical = true AND e.isDigital = true")
    long countBothByUserId(@Param("userId") Long userId);

    @Query("SELECT e.category.categoryId, e.category.name, COUNT(e) FROM Entrepreneurship e GROUP BY e.category.categoryId, e.category.name ORDER BY COUNT(e) DESC")
    List<Object[]> countByCategoryGrouped();

    @Query("SELECT COUNT(e) FROM Entrepreneurship e WHERE e.isPhysical = true AND e.isDigital = false")
    long countPhysicalOnly();

    @Query("SELECT COUNT(e) FROM Entrepreneurship e WHERE e.isDigital = true AND e.isPhysical = false")
    long countDigitalOnly();

    @Query("SELECT COUNT(e) FROM Entrepreneurship e WHERE e.isPhysical = true AND e.isDigital = true")
    long countBoth();

    List<Entrepreneurship> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT YEAR(e.createdAt), MONTH(e.createdAt), COUNT(e) FROM Entrepreneurship e GROUP BY YEAR(e.createdAt), MONTH(e.createdAt) ORDER BY YEAR(e.createdAt), MONTH(e.createdAt)")
    List<Object[]> countByMonth();
}
