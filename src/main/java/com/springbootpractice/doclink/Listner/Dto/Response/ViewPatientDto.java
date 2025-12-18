package com.springbootpractice.doclink.Listner.Dto.Response;

import com.springbootpractice.doclink.Kernel.Enums.GenderType;
import lombok.Data;

@Data
public class ViewPatientDto {
    private Long patientId;
    private Long userId;
    private String gpsLocation;
    private String patientName;
    private GenderType gender;
    private String email;
    private String phoneNumber;
}
