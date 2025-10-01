package com.springbootpractice.doclink.Kernal.Relations;

import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import com.springbootpractice.doclink.Kernal.Entity.Hospital;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_availability",
        indexes = {
                @Index(name = "idx_da_doctor", columnList = "doctor_id"),
                @Index(name = "idx_da_hospital", columnList = "hospital_id"),
                @Index(name = "idx_da_start_time", columnList = "start_time")
        })
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"doctor", "hospital"})
public class Doctor_availability {

    @EmbeddedId
    private DoctorAvailabilityId id;

    @MapsId("doctorId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @MapsId("hospitalId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "availability", nullable = false)
    private Boolean availability = Boolean.TRUE;

    @Embeddable
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class DoctorAvailabilityId implements Serializable {
        private static final long serialVersionUID = 1L;

        @Column(name = "hospital_id", nullable = false)
        private Long hospitalId;

        @Column(name = "doctor_id", nullable = false)
        private Long doctorId;
    }
}