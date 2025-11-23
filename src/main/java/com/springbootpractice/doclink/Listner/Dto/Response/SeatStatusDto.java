package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatStatusDto {
    private Integer seatNumber;
    private String status; // "AVAILABLE", "BOOKED"
    private Long appointmentId; // Useful if the doctor wants to click to see more details
    private String patientName; // Sensitive data
    private String patientContact; // Sensitive data
}