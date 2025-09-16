package com.springbootpractice.doclink.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "hospital_doctors")
@Data @NoArgsConstructor @AllArgsConstructor
@IdClass(HospitalDoctor.PK.class)
public class HospitalDoctor {

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

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class PK implements Serializable {
        private Long hospital;
        private Long doctor;
    }
}

