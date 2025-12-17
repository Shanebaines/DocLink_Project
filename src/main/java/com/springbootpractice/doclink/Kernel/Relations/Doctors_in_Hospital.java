package com.springbootpractice.doclink.Kernel.Relations;

import com.springbootpractice.doclink.Kernel.Entity.Doctor;
import com.springbootpractice.doclink.Kernel.Entity.Hospital;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "hospital_doctors")
@Data @NoArgsConstructor @AllArgsConstructor
@IdClass(Doctors_in_Hospital.PK.class)
public class Doctors_in_Hospital {

    @Id
    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Id
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "consultation_fee", precision = 10, scale = 2)
    private java.math.BigDecimal consultationFee;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class PK implements Serializable {
        private Long hospital;
        private Long doctor;
    }
}

