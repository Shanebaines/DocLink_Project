package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorAppointmentDetailDto {
    private Long appointmentId;
    private String patientName;
    private String patientContact;
    private String insuranceNumber;
    private String gender;
    private String reasonForVisit;
    private String status;
}