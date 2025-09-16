package com.springbootpractice.doclink.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hospital_pharmacies",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_hospital_pharmacy", columnNames = "hospital_id"),
                @UniqueConstraint(name = "uk_hosp_pharm_license", columnNames = "license_number")
        })
@Data @NoArgsConstructor @AllArgsConstructor
public class HospitalPharmacy {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_pharmacy_id")
    private Long hospitalPharmacyId;

    @OneToOne
    @JoinColumn(name = "hospital_id", nullable = false, unique = true)
    private Hospital hospital;

    @Column(name = "operating_hours", columnDefinition = "JSONB")
    private String operatingHours;

    @Column(name = "pharmacist_in_charge", length = 200)
    private String pharmacistInCharge;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
}

