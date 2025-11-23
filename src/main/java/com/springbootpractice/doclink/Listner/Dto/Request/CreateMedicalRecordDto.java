package com.springbootpractice.doclink.Listner.Dto.Request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateMedicalRecordDto {
    private Long patientId;
    private Long hospitalId;
    private Long doctorId;
    private String symptoms;
    private String diagnosis;//include report retails
    private String treatment;//include number of dates need to rest
    private LocalDate visitDate;
}
