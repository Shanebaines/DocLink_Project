package com.springbootpractice.doclink.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescription_medications")
@Data @NoArgsConstructor @AllArgsConstructor
public class PrescriptionMedication {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescription_medication_id")
    private Long prescriptionMedicationId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(optional = false)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    private String dosage;
    private String frequency;

    @Column(name = "duration_days")
    private Integer durationDays;

    private Integer quantity;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

