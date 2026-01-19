package com.springbootpractice.doclink.Listner.Dto.Request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMedicalRecordDto {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    // Optional: Include medical report data
    @Valid
    private CreateMedicalReportDto medicalReport;

    // Optional: Include prescription data
    @Valid
    private CreatePrescriptionDto prescription;

    // At least one must be provided - validated in service
}