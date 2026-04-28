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
@Table(name = "entrepreneurship_gallery")
public class EntrepreneurshipGallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gallery_id")
    private Long galleryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrepreneurship_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_gallery_entrepreneurship"))
    private Entrepreneurship entrepreneurship;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
