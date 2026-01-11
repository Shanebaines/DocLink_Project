package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;
import com.springbootpractice.doclink.Kernel.Enums.PrescriptionStatusType;
import java.time.LocalDate;
import java.util.List;

@Data
public class PrescriptionResponse {
    private Long prescriptionId;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private LocalDate prescriptionDate;
    private String diagnosis;
    private String instructions;
    private PrescriptionStatusType status;
    private List<PrescriptionMedicationResponse> medications;
}