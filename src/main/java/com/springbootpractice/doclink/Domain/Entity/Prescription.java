package com.springbootpractice.doclink.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.springbootpractice.doclink.Domain.Enums.PrescriptionStatusType;

@Entity
@Table(name = "prescriptions",
        indexes = {
                @Index(name = "idx_prescriptions_date", columnList = "prescription_date"),
                @Index(name = "idx_prescriptions_patient", columnList = "patient_id"),
                @Index(name = "idx_prescriptions_status", columnList = "status")
        })
@Data @NoArgsConstructor @AllArgsConstructor
public class Prescription {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescription_id")
    private Long prescriptionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(name = "prescription_date", nullable = false)
    private LocalDate prescriptionDate;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Enumerated(EnumType.STRING)
    private PrescriptionStatusType status = PrescriptionStatusType.active;

    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
}

