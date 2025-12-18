package com.springbootpractice.doclink.Listner.Dto.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrescriptionMedicationDto {

    @NotNull(message = "Medication ID is required")
    private Long medicationId;

    private String dosage;

    private String frequency;

    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String instructions;
}