package com.springbootpractice.doclink.Listner.Dto.Response;

import com.springbootpractice.doclink.Kernel.Enums.AppointmentStatusType;
import lombok.Data;

@Data
public class seatDto {
    private Integer seatNumber;
    private AppointmentStatusType status;
}
