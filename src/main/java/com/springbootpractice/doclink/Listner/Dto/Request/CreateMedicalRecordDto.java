package com.springbootpractice.doclink.Listner.Dto.Request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateMedicalRecordDto {
    private Long patientId;
    private Long doctorId;
    private LocalDate visitDate;
    private String symptoms;
    private String diagnosis;
    private String treatment;
    private String notes;
    private String vitalSigns;
}
