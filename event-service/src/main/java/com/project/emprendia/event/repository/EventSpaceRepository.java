package com.project.emprendia.event.repository;

import com.project.emprendia.event.domain.EventSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventSpaceRepository extends JpaRepository<EventSpace, Long> {
    List<EventSpace> findByEvent_EventId(Long eventId);
    List<EventSpace> findByEvent_EventIdAndIsAvailable(Long eventId, Boolean isAvailable);
}
