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
@Table(name = "event_invitation",
        uniqueConstraints = @UniqueConstraint(name = "uk_event_invitation_unique",
                columnNames = {"event_id", "entrepreneurship_id"}))
public class EventInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitation_id")
    private Long invitationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_invitation_event"))
    private Event event;

    @Column(name = "entrepreneurship_id", nullable = false)
    private Long entrepreneurshipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_space_id",
            foreignKey = @ForeignKey(name = "fk_invitation_space"))
    private EventSpace eventSpace;

    @Column(name = "invitation_status_id", nullable = false)
    private Long invitationStatusId;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
    }
}
