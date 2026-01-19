package com.springbootpractice.doclink.Listner.Dto.Request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreatePrescriptionDto {

    private LocalDate prescriptionDate; // Optional, defaults to today

    private String diagnosis;

    private String instructions;

    @NotEmpty(message = "At least one medication is required")
    @Valid
    private List<PrescriptionMedicationDto> medications;
}