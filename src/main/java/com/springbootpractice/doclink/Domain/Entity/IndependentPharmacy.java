package com.springbootpractice.doclink.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.springbootpractice.doclink.Domain.Enums.PharmacyType;

@Entity
@Table(name = "independent_pharmacies",
        uniqueConstraints = @UniqueConstraint(name = "uk_ind_pharm_license", columnNames = "license_number"),
        indexes = {})
@Data @NoArgsConstructor @AllArgsConstructor
public class IndependentPharmacy {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pharmacy_id")
    private Long pharmacyId;

    @Column(name = "pharmacy_name", nullable = false, length = 200)
    private String pharmacyName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String address;

    @Column(name = "gps_location", columnDefinition = "POINT")
    private String gpsLocation;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    private String email;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(name = "operating_hours", columnDefinition = "JSONB")
    private String operatingHours;

    @Column(name = "owner_name", length = 200)
    private String ownerName;

    @Column(name = "owner_id_number")
    private Integer ownerIdNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "pharmacy_type")
    private PharmacyType pharmacyType;

    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
}

