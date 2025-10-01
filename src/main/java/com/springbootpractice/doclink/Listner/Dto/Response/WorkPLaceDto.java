package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;

@Data
public class WorkPLaceDto {
    private String hospitalName;
    private String hospitalAddress;
    private String phoneNumber;
    private Integer availableSeats;
    private Integer totalSeats;
    private String timePeriod;
    private String  availability;
}
