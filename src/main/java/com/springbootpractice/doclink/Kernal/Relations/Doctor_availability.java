package com.springbootpractice.doclink.Kernal.Relations;

import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import com.springbootpractice.doclink.Kernal.Entity.Hospital;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "doctor_availability",
        indexes = {
                @Index(name = "idx_da_doctor", columnList = "doctor_id"),
                @Index(name = "idx_da_hospital", columnList = "hospital_id"),
                @Index(name = "idx_da_day_time", columnList = "day_of_week, start_time"),
                @Index(name = "idx_da_availability", columnList = "availability")
        })
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"doctor", "hospital"})
public class Doctor_availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "availability", nullable = false)
    private Boolean availability = Boolean.TRUE;

    // Optional: For tracking specific date ranges when this schedule is valid
    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;

    @Column(name = "effective_until")
    private LocalDateTime effectiveUntil;

    // Helper method to check if the doctor is available on a specific date/time
    public boolean isAvailableOn(LocalDateTime dateTime) {
        if (!availability) return false;

        boolean dayMatches = dateTime.getDayOfWeek() == dayOfWeek;
        boolean timeInRange = !dateTime.toLocalTime().isBefore(startTime)
                && !dateTime.toLocalTime().isAfter(endTime);

        boolean withinEffectivePeriod = true;
        if (effectiveFrom != null && dateTime.isBefore(effectiveFrom)) {
            withinEffectivePeriod = false;
        }
        if (effectiveUntil != null && dateTime.isAfter(effectiveUntil)) {
            withinEffectivePeriod = false;
        }

        return dayMatches && timeInRange && withinEffectivePeriod;
    }
}