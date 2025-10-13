package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;

@Data
public class ViewDoctorsDto {
    private String image;
    private Long doctorId;
    private String name;
    private String specialization;
}
