package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;
import java.time.DayOfWeek;

@Data
public class AvailableSlotsDto {
    private Long slotId;
    private DayOfWeek dayOfWeek;
    private Integer totalSeats;
    private String timePeriod;
}