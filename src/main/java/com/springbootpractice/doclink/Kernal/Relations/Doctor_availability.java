package com.springbootpractice.doclink.Kernal.Relations;

import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import com.springbootpractice.doclink.Kernal.Entity.Hospital;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "doctor_availability",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_da_slot_period",
                        columnNames = {"slot_id", "effective_from", "effective_until"}
                )
        },
        indexes = {
                @Index(name = "idx_da_doctor", columnList = "doctor_id"),
                @Index(name = "idx_da_hospital", columnList = "hospital_id"),
                @Index(name = "idx_da_slot", columnList = "slot_id"),
                @Index(name = "idx_da_availability", columnList = "availability"),
                @Index(name = "idx_da_period", columnList = "effective_from, effective_until")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"doctor", "hospital", "slot"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Doctor_availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_da_doctor"))
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hospital_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_da_hospital"))
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "slot_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_da_slot"))
    private Doctor_time_slots slot;

    @Column(name = "availability", nullable = false)
    @NotNull
    @Builder.Default
    private Boolean availability = Boolean.TRUE;

    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;

    @Column(name = "effective_until")
    private LocalDateTime effectiveUntil;

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
        if (slot == null) throw new IllegalArgumentException("slot must not be null");
        if (doctor == null || hospital == null)
            throw new IllegalArgumentException("doctor and hospital must not be null");
        if (slot.getDoctor() != null && !doctor.equals(slot.getDoctor()))
            throw new IllegalArgumentException("doctor in availability must match slot.doctor");
        if (slot.getHospital() != null && !hospital.equals(slot.getHospital()))
            throw new IllegalArgumentException("hospital in availability must match slot.hospital");
        if (effectiveFrom != null && effectiveUntil != null && effectiveUntil.isBefore(effectiveFrom))
            throw new IllegalArgumentException("effectiveUntil must be after or equal to effectiveFrom");
    }

}