package com.springbootpractice.doclink.Kernal.Entity;

import com.springbootpractice.doclink.Kernal.Relations.Doctor_time_slots;
import com.springbootpractice.doclink.Kernal.Enums.AppointmentStatusType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments",
        indexes = {
                @Index(name = "idx_appointments_date", columnList = "appointment_date"),
                @Index(name = "idx_appointments_patient", columnList = "patient_id"),
                @Index(name = "idx_appointments_doctor", columnList = "doctor_id"),
                @Index(name = "idx_appointments_status", columnList = "status")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    /**
     * Link to the doctor’s weekly slot this appointment belongs to.
     * The slot implicitly defines doctor, hospital, day, and time range.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "time_slots_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_apt_time_slot"))
    private Doctor_time_slots timeSlot;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    private AppointmentStatusType status = AppointmentStatusType.scheduled;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "consultation_fee", precision = 10, scale = 2)
    private BigDecimal consultationFee;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Business‑rule enforcement before persisting/updating.
     */
    @PrePersist
    @PreUpdate
    private void validate() {
        if (timeSlot == null)
            throw new IllegalArgumentException("Appointment must have a valid doctor time slot");

        // Ensure doctor/hospital consistency
        if (doctor != null && !doctor.equals(timeSlot.getDoctor()))
            throw new IllegalArgumentException("Doctor in appointment must match time slot’s doctor");
        if (hospital != null && !hospital.equals(timeSlot.getHospital()))
            throw new IllegalArgumentException("Hospital in appointment must match time slot’s hospital");

        // Check seat validity
        Integer total = timeSlot.getTotalSeats();
        if (seatNumber == null || seatNumber < 1)
            throw new IllegalArgumentException("Seat number must be >= 1");
        if (total != null && seatNumber > total)
            throw new IllegalArgumentException("Seat number cannot exceed total seats for the time slot");
    }
}