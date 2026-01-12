package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;

@Data
public class BookmarkDoctorDto {
    private Long doctorId;
    private String name;
    private String specialization;
    private String licenseNumber;
    private Integer yearsExperience;
    private String qualification;
    private String email;
    private String phoneNumber;
    private String image;
}
