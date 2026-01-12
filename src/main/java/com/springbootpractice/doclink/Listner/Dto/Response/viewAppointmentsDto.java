package com.springbootpractice.doclink.Listner.Dto.Response;

import com.springbootpractice.doclink.Kernal.Enums.AppointmentStatusType;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Data
public class viewAppointmentsDto {
    private Long hospitalId;
    private String hospitalName;
    private Long DoctorId;
    private String DoctorName;
    private Integer seatNumber;
    private String timeSlot;
    private LocalDateTime availableTime;
    private DayOfWeek dayOfWeek;
    private AppointmentStatusType appointmentStatus;
}
