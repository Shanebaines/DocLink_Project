package com.springbootpractice.doclink.Listner.Dto.Request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateAppointmentStatusByDoctorDto {
    Long slot_id;
    LocalDate appointment_date;
    Integer seat_number;
}
