package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;

@Data
public class PrescriptionMedicationResponse_Pharmacy {
    private String medicationName;
    private String dosage;
    private String frequency;
    private Integer durationDays;
    private Integer quantity;
}
