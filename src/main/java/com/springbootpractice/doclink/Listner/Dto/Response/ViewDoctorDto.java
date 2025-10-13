package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;

import java.util.List;

@Data
public class ViewDoctorDto {
    private String image;
    private String doctorName;
    private String licenseNumber;
    private Integer yearOfExperience;
    private String specialization;
    private String qualification;
    private String phoneNumber;
    private String email;
    private String address;
    private List<WorkPLaceDto> workPlaces;
}
