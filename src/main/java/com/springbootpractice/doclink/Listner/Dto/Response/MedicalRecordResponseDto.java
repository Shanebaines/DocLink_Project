package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class MedicalRecordResponseDto {

    private Long medicalRecordId;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Boolean hasMedicalReport;
    private Boolean hasPrescription;
    private MedicalReportResponseDto medicalReport;
    private PrescriptionResponseDto prescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}