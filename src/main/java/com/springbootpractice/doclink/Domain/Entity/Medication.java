package com.springbootpractice.doclink.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "medications")
@Data @NoArgsConstructor @AllArgsConstructor
public class Medication {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medication_id")
    private Long medicationId;

    @Column(name = "medication_name", nullable = false, length = 200)
    private String medicationName;

    @Column(name = "generic_name", length = 200)
    private String genericName;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 200)
    private String manufacturer;

    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
}

