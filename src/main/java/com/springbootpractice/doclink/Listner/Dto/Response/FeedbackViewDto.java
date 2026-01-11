package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FeedbackViewDto {
    private Long feedbackId;
    private String patientName; // Will be "Anonymous" if isAnonymous is true
    private Integer rating;
    private String comment;
    private LocalDateTime date;
}