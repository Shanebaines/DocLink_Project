package com.springbootpractice.doclink.Kernel.Entity;

import com.springbootpractice.doclink.Kernel.Relations.MedicalRecord;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_reports",
        indexes = {
                @Index(name = "idx_medical_reports_visit_date", columnList = "visit_date")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalReport {

    @Id
    @Column(name = "report_id")
    private Long reportId; // Same as medicalRecordId - NOT auto-generated

    @OneToOne(optional = false)
    @MapsId // This makes reportId use the same value as medicalRecord's ID
    @JoinColumn(name = "report_id")
    private MedicalRecord medicalRecord;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String treatment;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @Column(name = "vital_signs", columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private String vitalSigns;

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