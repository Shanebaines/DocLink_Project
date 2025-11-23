package com.springbootpractice.doclink.Listner.Dto.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.List;
import java.time.LocalDate;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewSlotDto {
    private Long slotId; // <-- ADD THIS (Required for Doctor's click action)

    private Long hospitalId;
    private DayOfWeek dayOfWeek;
    private String hospitalName;
    private String doctorName;
    private Boolean availability;
    private Integer freeSeats;
    private LocalDate date;
    private Integer totalSeats;
    private String timePeriod;
    private List<seatDto> seats;

}
