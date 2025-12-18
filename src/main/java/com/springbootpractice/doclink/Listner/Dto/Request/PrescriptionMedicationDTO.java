package com.springbootpractice.doclink.Listner.Dto.Request;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class PrescriptionMedicationDTO {

    @NotNull(message = "Medication ID is required")
    private Long medicationId;

    private String dosage;

    private String frequency;

    private Integer durationDays;

    private Integer quantity;

    private String instructions;
}