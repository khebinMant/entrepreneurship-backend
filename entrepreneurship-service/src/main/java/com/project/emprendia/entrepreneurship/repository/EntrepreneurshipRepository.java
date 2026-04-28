package com.project.emprendia.entrepreneurship.repository;

import com.project.emprendia.entrepreneurship.domain.Entrepreneurship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntrepreneurshipRepository extends JpaRepository<Entrepreneurship, Long> {
    List<Entrepreneurship> findByUserId(Long userId);
    List<Entrepreneurship> findByCategory_CategoryId(Long categoryId);
}
