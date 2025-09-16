package com.springbootpractice.doclink.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.springbootpractice.doclink.Domain.Enums.EmergencyPriorityType;

@Entity
@Table(name = "emergency_requests",
        indexes = {
                @Index(name = "idx_emergency_requests_status", columnList = "status"),
                @Index(name = "idx_emergency_requests_priority", columnList = "priority")
        })
@Data @NoArgsConstructor @AllArgsConstructor
public class EmergencyRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "ambulance_id")
    private AmbulanceService ambulance;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @Column(name = "request_time")
    private LocalTime requestTime;

    @Column(name = "patient_location", columnDefinition = "TEXT")
    private String patientLocation;

    @Column(name = "gps_coordinates", columnDefinition = "POINT")
    private String gpsCoordinates;

    @Column(name = "emergency_type", length = 100)
    private String emergencyType;

    @Column(length = 50)
    private String status = "pending";

    @Enumerated(EnumType.STRING)
    private EmergencyPriorityType priority = EmergencyPriorityType.medium;

    @Column(name = "estimated_distance", precision = 10, scale = 2)
    private BigDecimal estimatedDistance;

    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
}

