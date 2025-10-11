package com.springbootpractice.doclink.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors",
        uniqueConstraints = @UniqueConstraint(name = "uk_doctor_license", columnNames = "license_number"),
        indexes = {
                @Index(name = "idx_doctors_user_id", columnList = "user_id"),
                @Index(name = "idx_doctors_license", columnList = "license_number"),
                @Index(name = "idx_doctors_specialization", columnList = "specialization")
        })
@Data @NoArgsConstructor @AllArgsConstructor
public class Doctor {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Long doctorId;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String specialization;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    private String qualification;

    @Column(name = "consultation_fee", precision = 10, scale = 2)
    private java.math.BigDecimal consultationFee;

    @Column(name = "availability_schedule", columnDefinition = "JSONB")
    private String availabilitySchedule;
}
