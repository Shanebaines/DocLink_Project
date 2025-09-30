package com.springbootpractice.doclink.Kernal.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import com.springbootpractice.doclink.Kernal.Enums.GenderType;

@Entity
@Table(name = "patients",
        uniqueConstraints = @UniqueConstraint(name = "unique_patient_user", columnNames = "user_id"),
        indexes = {
                @Index(name = "idx_patients_user_id", columnList = "user_id"),
                @Index(name = "idx_patients_insurance", columnList = "insurance_number")
        })
@Data @NoArgsConstructor @AllArgsConstructor
public class Patient {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long patientId;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "insurance_number", length = 50)
    private String insuranceNumber;
}
