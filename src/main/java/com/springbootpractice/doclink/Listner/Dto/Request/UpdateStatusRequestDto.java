package com.springbootpractice.doclink.Listner.Dto.Request;

import com.springbootpractice.doclink.Kernel.Enums.AppointmentStatusType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequestDto {
    @NotNull private AppointmentStatusType newStatus;
    private String notes;
}