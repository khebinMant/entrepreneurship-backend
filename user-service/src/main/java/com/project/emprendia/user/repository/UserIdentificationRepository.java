package com.project.emprendia.user.repository;

import com.project.emprendia.user.domain.UserIdentification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserIdentificationRepository extends JpaRepository<UserIdentification, Long> {
    List<UserIdentification> findByUser_UserId(Long userId);
}
