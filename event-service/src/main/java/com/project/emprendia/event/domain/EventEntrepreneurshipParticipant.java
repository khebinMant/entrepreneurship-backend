package com.project.emprendia.event.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_entrepreneurship_participant")
public class EventEntrepreneurshipParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_participant_id")
    private Long eventParticipantId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "entrepreneurship_id", nullable = false)
    private Long entrepreneurshipId;

    @Column(name = "space_code", length = 10)
    private String spaceCode;

    @Column(name = "participation_status_id", nullable = false)
    private Long participationStatusId;

    @Column(name = "invited_at")
    private LocalDateTime invitedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @PrePersist
    protected void onCreate() {
        invitedAt = LocalDateTime.now();
    }
}
