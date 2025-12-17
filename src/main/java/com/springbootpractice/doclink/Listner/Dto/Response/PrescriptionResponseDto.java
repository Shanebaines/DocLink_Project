package com.springbootpractice.doclink.Listner.Dto.Response;

import com.springbootpractice.doclink.Kernel.Enums.PrescriptionStatusType;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PrescriptionResponseDto {

    private Long prescriptionId; // Same as medicalRecordId
    private LocalDate prescriptionDate;
    private String diagnosis;
    private String instructions;
    private PrescriptionStatusType status;
    private List<PrescriptionMedicationResponseDto> medications;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}