package com.springbootpractice.doclink.Kernal.Relations;

import java.time.LocalDateTime;

import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import com.springbootpractice.doclink.Kernal.Entity.Patient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "patient_doctor_bookmarks",
        uniqueConstraints = @UniqueConstraint(name = "uk_patient_doctor", columnNames = {"patient_id", "doctor_id"}),
        indexes = {
                @Index(name = "idx_bookmark_patient", columnList = "patient_id"),
                @Index(name = "idx_bookmark_doctor", columnList = "doctor_id")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDoctorBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long bookmarkId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
