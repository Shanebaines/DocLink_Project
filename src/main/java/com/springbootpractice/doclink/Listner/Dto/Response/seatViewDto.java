package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;

@Data
public class seatViewDto {
    Long patientId;
    String patientName;
    seatDto seat;
}
