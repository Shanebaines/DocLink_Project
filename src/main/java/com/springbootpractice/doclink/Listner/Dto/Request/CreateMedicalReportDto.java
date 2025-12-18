package com.springbootpractice.doclink.Listner.Dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateMedicalReportDto {

    @NotNull(message = "Visit date is required")
    private LocalDate visitDate;

    private String symptoms;

    private String diagnosis;

    private String treatment;

    private String recommendation;

    private String vitalSigns; // JSON string
}