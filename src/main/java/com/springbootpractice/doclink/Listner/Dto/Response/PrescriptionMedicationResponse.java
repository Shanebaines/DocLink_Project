package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;

@Data
public class PrescriptionMedicationResponse {
    private Long prescriptionMedicationId;
    private Long medicationId;
    private String medicationName;
    private String dosage;
    private String frequency;
    private Integer durationDays;
    private Integer quantity;
    private String instructions;
}
