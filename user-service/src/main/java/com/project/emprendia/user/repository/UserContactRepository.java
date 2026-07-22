package com.project.emprendia.user.repository;

import com.project.emprendia.user.domain.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserContactRepository extends JpaRepository<UserContact, Long> {
    List<UserContact> findByUser_UserId(Long userId);
    Optional<UserContact> findByUser_UserIdAndIsPrimaryTrue(Long userId);
    Optional<UserContact> findByContactTypeIdAndContactValue(Long contactTypeId, String contactValue);
}
