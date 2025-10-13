package com.springbootpractice.doclink.Kernal.Relations;

import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import com.springbootpractice.doclink.Kernal.Entity.Hospital;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "doctor_time_slots",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_doc_hosp_day_start",
                        columnNames = {"doctor_id", "hospital_id", "day_of_week", "start_time"}
                )
        },
        indexes = {
                @Index(name = "idx_doc_hosp_day", columnList = "doctor_id, hospital_id, day_of_week"),
                @Index(name = "idx_hospital_day", columnList = "hospital_id, day_of_week")
        }
)
@Check(constraints = "end_time > start_time AND total_seats > 0")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"doctor", "hospital"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Doctor_time_slots {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_slot_doctor"))
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hospital_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_slot_hospital"))
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    @NotNull
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    @NotNull
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @NotNull
    private LocalTime endTime;

    @Column(name = "total_seats", nullable = false)
    @NotNull
    @Min(1)
    private Integer totalSeats;

    @Column(name = "availability", nullable = false)
    @NotNull
    @Builder.Default
    private Boolean availability = Boolean.TRUE;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (startTime != null && endTime != null && !endTime.isAfter(startTime))
            throw new IllegalArgumentException("endTime must be strictly after startTime");
        if (totalSeats != null && totalSeats <= 0)
            throw new IllegalArgumentException("totalSeats must be > 0");
    }
}