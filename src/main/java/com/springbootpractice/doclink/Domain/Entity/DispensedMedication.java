package com.springbootpractice.doclink.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispensed_medications")
@Data @NoArgsConstructor @AllArgsConstructor
public class DispensedMedication {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dispensed_id")
    private Long dispensedId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(optional = false)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    @ManyToOne
    @JoinColumn(name = "hospital_pharmacy_id")
    private HospitalPharmacy hospitalPharmacy;

    @ManyToOne
    @JoinColumn(name = "independent_pharmacy_id")
    private IndependentPharmacy independentPharmacy;

    @Column(name = "dispensed_date")
    private LocalDate dispensedDate;

    @Column(name = "quantity_dispensed", nullable = false)
    private Integer quantityDispensed;

    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "pharmacist_name", length = 200)
    private String pharmacistName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // NOTE: DB check constraint ensures exactly one of the pharmacy fields is set.
}

