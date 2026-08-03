package com.project.emprendia.event.repository;

import com.project.emprendia.event.domain.EventInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventInvitationRepository extends JpaRepository<EventInvitation, Long> {
    List<EventInvitation> findByEvent_EventId(Long eventId);
    List<EventInvitation> findByEvent_EventIdAndInvitationStatusId(Long eventId, Long invitationStatusId);
    boolean existsByEvent_EventIdAndEntrepreneurshipId(Long eventId, Long entrepreneurshipId);

    long countByEvent_CreatedByUserId(Long userId);

    List<EventInvitation> findByEntrepreneurshipId(Long entrepreneurshipId);

    @Query("SELECT i.invitationStatusId, COUNT(i) FROM EventInvitation i WHERE i.entrepreneurshipId = :entrepreneurshipId GROUP BY i.invitationStatusId")
    List<Object[]> countByStatusGroupedByEntrepreneurship(@Param("entrepreneurshipId") Long entrepreneurshipId);

    @Query("SELECT i.invitationStatusId, COUNT(i) FROM EventInvitation i GROUP BY i.invitationStatusId")
    List<Object[]> countByStatusGrouped();
}
