package com.project.emprendia.entrepreneurship.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "entrepreneurship")
public class Entrepreneurship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entrepreneurship_id")
    private Long entrepreneurshipId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_entrepreneurship_category"))
    private Category category;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @Column(name = "is_physical", nullable = false)
    @Builder.Default
    private Boolean isPhysical = false;

    @Column(name = "is_digital", nullable = false)
    @Builder.Default
    private Boolean isDigital = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "entrepreneurship", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EntrepreneurshipLocation> locations = new ArrayList<>();

    @OneToMany(mappedBy = "entrepreneurship", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EntrepreneurshipSocialLink> socialLinks = new ArrayList<>();

    @OneToMany(mappedBy = "entrepreneurship", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EntrepreneurshipGallery> gallery = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
