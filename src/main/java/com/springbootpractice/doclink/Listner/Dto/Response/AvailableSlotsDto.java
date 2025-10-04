package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;

@Data
public class AvailableSlotsDto {
    private Integer availableSeats;
    private Integer totalSeats;
    private String timePeriod;
    private String  availability;
}
