package com.springbootpractice.doclink.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.springbootpractice.doclink.Domain.Enums.TestStatusType;

@Entity
@Table(name = "lab_tests",
        indexes = {
                @Index(name = "idx_lab_tests_date", columnList = "scheduled_date"),
                @Index(name = "idx_lab_tests_patient", columnList = "patient_id"),
                @Index(name = "idx_lab_tests_status", columnList = "status")
        })
@Data @NoArgsConstructor @AllArgsConstructor
public class LabTest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private Long testId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "laboratory_id", nullable = false)
    private Laboratory laboratory;

    @Column(name = "test_name", nullable = false, length = 200)
    private String testName;

    @Column(name = "test_type", length = 100)
    private String testType;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;

    @Enumerated(EnumType.STRING)
    private TestStatusType status = TestStatusType.scheduled;

    @Column(precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
}

