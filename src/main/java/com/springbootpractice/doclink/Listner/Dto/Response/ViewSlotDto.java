package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ViewSlotDto {
    private String hospitalName;
    private String doctorName;
    private LocalDate date;
    private  Boolean availability;
    private Integer freeSeats;
    private Integer totalSeats;
    private String timePeriod;
    private List<seatDto> seats;
}
