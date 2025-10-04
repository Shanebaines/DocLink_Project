package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;

import java.util.List;

@Data
public class WorkPLaceDto {
    private Long hospitalId;
    private String hospitalName;
    private String gpsLocation;
    private String hospitalAddress;
    private String phoneNumber;
    private List<AvailableSlotsDto> availableSlots;
}
