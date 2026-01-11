package com.springbootpractice.doclink.Listner.Dto.Request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreatePrescriptionRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    private LocalDate prescriptionDate;

    private String diagnosis;

    private String instructions;

    @NotEmpty(message = "At least one medication is required")
    private List<PrescriptionMedicationDTO> medications;
}