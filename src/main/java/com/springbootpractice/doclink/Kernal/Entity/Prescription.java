package com.springbootpractice.doclink.Kernal.Entity;

import com.springbootpractice.doclink.Kernal.Enums.PrescriptionStatusType;
import com.springbootpractice.doclink.Kernal.Relations.MedicalRecord;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescriptions",
        indexes = {
                @Index(name = "idx_prescriptions_date", columnList = "prescription_date"),
                @Index(name = "idx_prescriptions_status", columnList = "status")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @Column(name = "prescription_id")
    private Long prescriptionId; // Same as medicalRecordId - NOT auto-generated

    @OneToOne(optional = false)
    @MapsId // This makes prescriptionId use the same value as medicalRecord's ID
    @JoinColumn(name = "prescription_id")
    private MedicalRecord medicalRecord;

    @Column(name = "prescription_date", nullable = false)
    private LocalDate prescriptionDate;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PrescriptionStatusType status;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PrescriptionMedication> prescriptionMedications = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to add medication
    public void addPrescriptionMedication(PrescriptionMedication medication) {
        prescriptionMedications.add(medication);
        medication.setPrescription(this);
    }
}