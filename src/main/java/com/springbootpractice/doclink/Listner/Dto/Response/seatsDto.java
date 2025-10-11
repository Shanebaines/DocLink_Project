package com.springbootpractice.doclink.Listner.Dto.Response;

import com.springbootpractice.doclink.Kernal.Enums.AppointmentStatusType;
import lombok.Data;

@Data
public class seatsDto {
    private Integer seatNumber;
    private AppointmentStatusType status;
}
