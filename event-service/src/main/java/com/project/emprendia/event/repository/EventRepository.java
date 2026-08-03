package com.project.emprendia.event.repository;

import com.project.emprendia.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCreatedByUserId(Long userId);

    long countByCreatedByUserId(Long userId);

    List<Event> findTop5ByCreatedByUserIdOrderByCreatedAtDesc(Long userId);

    long countByCreatedByUserIdAndStartDatetimeAfter(Long userId, LocalDateTime now);

    long countByCreatedByUserIdAndEndDatetimeBefore(Long userId, LocalDateTime now);

    @Query("SELECT e.eventTypeId, COUNT(e) FROM Event e WHERE e.createdByUserId = :userId GROUP BY e.eventTypeId")
    List<Object[]> countByEventTypeGroupedByCreator(@Param("userId") Long userId);

    @Query("SELECT e.eventVisibilityId, COUNT(e) FROM Event e WHERE e.createdByUserId = :userId GROUP BY e.eventVisibilityId")
    List<Object[]> countByVisibilityGroupedByCreator(@Param("userId") Long userId);

    long countByStartDatetimeAfter(LocalDateTime date);

    long countByEndDatetimeBefore(LocalDateTime date);

    @Query("SELECT e.eventTypeId, COUNT(e) FROM Event e GROUP BY e.eventTypeId")
    List<Object[]> countByEventTypeGrouped();

    @Query("SELECT e.eventVisibilityId, COUNT(e) FROM Event e GROUP BY e.eventVisibilityId")
    List<Object[]> countByVisibilityGrouped();

    List<Event> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT YEAR(e.createdAt), MONTH(e.createdAt), COUNT(e) FROM Event e GROUP BY YEAR(e.createdAt), MONTH(e.createdAt) ORDER BY YEAR(e.createdAt), MONTH(e.createdAt)")
    List<Object[]> countByMonth();
}
