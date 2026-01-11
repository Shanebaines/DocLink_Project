package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MedicalRecordDto {
    private Long recordId;
    private Long patientId;
    private Long doctorId;
    private LocalDate visitDate;
    private String symptoms;
    private String diagnosis;
    private String treatment;
    private String notes;
    private String vitalSigns;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}