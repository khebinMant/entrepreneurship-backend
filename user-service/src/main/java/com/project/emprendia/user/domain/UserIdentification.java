package com.project.emprendia.user.domain;

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
@Table(name = "user_identification",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_identification_unique",
                columnNames = {"identification_type_id", "identification_number"}))
public class UserIdentification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_identification_id")
    private Long userIdentificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_identification_user"))
    private AppUser user;

    @Column(name = "identification_type_id", nullable = false)
    private Long identificationTypeId;

    @Column(name = "identification_number", nullable = false, length = 30)
    private String identificationNumber;

    @Column(name = "issued_country_id")
    private Long issuedCountryId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
