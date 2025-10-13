package com.springbootpractice.doclink.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.springbootpractice.doclink.Domain.Enums.HospitalType;

@Entity
@Table(name = "hospitals",
        indexes = {
                @Index(name = "idx_hospitals_type", columnList = "hospital_type")
        })
@Data @NoArgsConstructor @AllArgsConstructor
public class Hospital {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_id")
    private Long hospitalId;

    @Column(name = "hospital_name", nullable = false, length = 200)
    private String hospitalName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String address;

    @Column(name = "gps_location", columnDefinition = "POINT")
    private String gpsLocation;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "hospital_type")
    private HospitalType hospitalType;

    @Column(name = "bed_capacity")
    private Integer bedCapacity;

    private String accreditation;

    @Column(name = "emergency_services")
    private Boolean emergencyServices = false;

    @Column(name = "has_pharmacy")
    private Boolean hasPharmacy = false;

    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
}

