package com.springbootpractice.doclink.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "hospital_pharmacy_medications")
@Data @NoArgsConstructor @AllArgsConstructor
@IdClass(HospitalPharmacyMedication.PK.class)
public class HospitalPharmacyMedication {

    @Id
    @ManyToOne
    @JoinColumn(name = "hospital_pharmacy_id", nullable = false)
    private HospitalPharmacy hospitalPharmacy;

    @Id
    @ManyToOne
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    @Column(name = "reorder_level")
    private Integer reorderLevel = 10;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class PK implements Serializable {
        private Long hospitalPharmacy;
        private Long medication;
    }
}

