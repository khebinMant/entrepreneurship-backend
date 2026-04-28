package com.project.emprendia.entrepreneurship.domain;

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
@Table(name = "entrepreneurship_portal",
        uniqueConstraints = @UniqueConstraint(name = "uk_entrepreneurship_subdomain",
                columnNames = "subdomain"))
public class EntrepreneurshipPortal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portal_id")
    private Long portalId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrepreneurship_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_portal_entrepreneurship"))
    private Entrepreneurship entrepreneurship;

    @Column(name = "subdomain", nullable = false, unique = true, length = 100)
    private String subdomain;

    @Column(name = "theme_id")
    private Long themeId;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
