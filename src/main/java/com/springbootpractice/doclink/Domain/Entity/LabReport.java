package com.springbootpractice.doclink.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_reports",
        uniqueConstraints = @UniqueConstraint(name = "unique_test_report", columnNames = "test_id"))
@Data @NoArgsConstructor @AllArgsConstructor
public class LabReport {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @OneToOne(optional = false)
    @JoinColumn(name = "test_id", nullable = false, unique = true)
    private LabTest test;

    @ManyToOne
    @JoinColumn(name = "medical_record_id")
    private MedicalRecord medicalRecord;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "test_results", columnDefinition = "TEXT")
    private String testResults;

    @Column(name = "normal_ranges", columnDefinition = "TEXT")
    private String normalRanges;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(length = 200)
    private String technician;

    @Column(length = 200)
    private String pathologist;

    @Column(name = "is_abnormal")
    private Boolean isAbnormal = false;

    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
}

