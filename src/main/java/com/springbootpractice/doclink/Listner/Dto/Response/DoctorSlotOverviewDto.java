package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DoctorSlotOverviewDto {
    private Long slotId;
    private String hospitalName;
    private String doctorName;
    private LocalDate date;
    private String timeRange;
    private Integer totalSeats;
    private Integer bookedSeats;
    private Boolean isSlotActive;
    private List<SeatStatusDto> seats; // The list of seats with patient info
}