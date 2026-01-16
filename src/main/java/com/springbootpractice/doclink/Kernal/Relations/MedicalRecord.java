package com.springbootpractice.doclink.Kernal.Relations;

import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import com.springbootpractice.doclink.Kernal.Entity.MedicalReport;
import com.springbootpractice.doclink.Kernal.Entity.Patient;
import com.springbootpractice.doclink.Kernal.Entity.Prescription;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records",
        indexes = {
                @Index(name = "idx_medical_records_patient", columnList = "patient_id"),
                @Index(name = "idx_medical_records_doctor", columnList = "doctor_id"),
                @Index(name = "idx_medical_records_created", columnList = "created_at")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medical_record_id")
    private Long medicalRecordId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "has_medical_report")
    private Boolean hasMedicalReport = false;

    @Column(name = "has_prescription")
    private Boolean hasPrescription = false;

    @OneToOne(mappedBy = "medicalRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MedicalReport medicalReport;

    @OneToOne(mappedBy = "medicalRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Prescription prescription;

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
}