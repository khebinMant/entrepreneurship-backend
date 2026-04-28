package com.project.emprendia.shared.domain;

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
@Table(name = "catalogue_value",
        uniqueConstraints = @UniqueConstraint(name = "uk_catalogue_value_code",
                columnNames = {"catalogue_type_id", "code"}))
public class CatalogueValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "catalogue_value_id")
    private Long catalogueValueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalogue_type_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_catalogue_value_type"))
    private CatalogueType catalogueType;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_value_id",
            foreignKey = @ForeignKey(name = "fk_catalogue_value_parent"))
    private CatalogueValue parentValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
