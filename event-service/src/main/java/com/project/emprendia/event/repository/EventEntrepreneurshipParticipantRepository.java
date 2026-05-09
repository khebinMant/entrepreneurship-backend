package com.project.emprendia.event.repository;

import com.project.emprendia.event.domain.EventEntrepreneurshipParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventEntrepreneurshipParticipantRepository extends JpaRepository<EventEntrepreneurshipParticipant, Long> {

    List<EventEntrepreneurshipParticipant> findByEventId(Long eventId);

    List<EventEntrepreneurshipParticipant> findByEntrepreneurshipId(Long entrepreneurshipId);

    Optional<EventEntrepreneurshipParticipant> findByEventIdAndEntrepreneurshipId(
            Long eventId,
            Long entrepreneurshipId
    );

    boolean existsByEventIdAndEntrepreneurshipId(Long eventId, Long entrepreneurshipId);

    @Query("SELECT COUNT(e) FROM EventEntrepreneurshipParticipant e " +
           "WHERE e.eventId = :eventId")
    long countParticipantsByEventId(@Param("eventId") Long eventId);

    void deleteByEventId(Long eventId);

    void deleteByEntrepreneurshipId(Long entrepreneurshipId);
}
