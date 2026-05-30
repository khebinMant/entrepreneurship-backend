package com.project.emprendia.event.repository;

import com.project.emprendia.event.domain.EventInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventInvitationRepository extends JpaRepository<EventInvitation, Long> {
    List<EventInvitation> findByEvent_EventId(Long eventId);
    List<EventInvitation> findByEvent_EventIdAndInvitationStatusId(Long eventId, Long invitationStatusId);
    boolean existsByEvent_EventIdAndEntrepreneurshipId(Long eventId, Long entrepreneurshipId);
}
