package com.springbootpractice.doclink.Listner.Dto.Request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateFeedbackDto {
    @NotNull
    private Long patientId;

    @NotNull
    private Long doctorId;


    @Min(1) @Max(5)
    private Integer rating;

    private String comment;

    @NotNull
    private Boolean isAnonymous; // User chooses this in UI
}