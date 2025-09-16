package com.springbootpractice.doclink.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.springbootpractice.doclink.Domain.Enums.AmbulanceType;
import com.springbootpractice.doclink.Domain.Enums.AmbulanceStatusType;

@Entity
@Table(name = "ambulance_services",
        indexes = {
                @Index(name = "idx_ambulances_status", columnList = "status"),
                @Index(name = "idx_ambulances_hospital", columnList = "hospital_id")
        })
@Data @NoArgsConstructor @AllArgsConstructor
public class AmbulanceService {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ambulance_id")
    private Long ambulanceId;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Column(name = "vehicle_number", nullable = false, unique = true, length = 50)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "ambulance_type")
    private AmbulanceType ambulanceType;

    @Column(name = "current_location", columnDefinition = "POINT")
    private String currentLocation;

    @Column(name = "gps_location", columnDefinition = "POINT")
    private String gpsLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AmbulanceStatusType status = AmbulanceStatusType.available;

    @Column(name = "driver_name", length = 200)
    private String driverName;

    @Column(name = "paramedic_name", length = 200)
    private String paramedicName;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Column(name = "service_charge", precision = 10, scale = 2)
    private BigDecimal serviceCharge;

    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
}

