package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MedicalReportResponseDto {

    private Long reportId; // Same as medicalRecordId
    private LocalDate visitDate;
    private String symptoms;
    private String diagnosis;
    private String treatment;
    private String recommendation;
    private String vitalSigns;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}