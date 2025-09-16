package com.springbootpractice.doclink.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "laboratories")
@Data @NoArgsConstructor @AllArgsConstructor
public class Laboratory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lab_id")
    private Long labId;

    @Column(name = "lab_name", nullable = false, length = 200)
    private String labName;

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

    @Column(name = "lab_type", length = 100)
    private String labType;

    @ElementCollection
    @CollectionTable(name = "laboratory_available_tests", joinColumns = @JoinColumn(name = "lab_id"))
    @Column(name = "test_name")
    private List<String> availableTests;

    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
}
