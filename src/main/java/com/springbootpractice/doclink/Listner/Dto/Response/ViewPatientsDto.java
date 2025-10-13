package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;

@Data
public class ViewPatientsDto {
    private Long patientId;
    private Long userId;
    private String firstName;
    private String lastName;
    private String address;
    private String gpsLocation;
}