package com.springbootpractice.doclink.Listner.Dto.Request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateAppointmentRequestDto {
    @NotNull private Long patientId;
    @NotNull private Long timeSlotId;
    @NotNull @FutureOrPresent private LocalDate appointmentDate;
    @NotNull private Integer seatNumber;
    private String reason;
}