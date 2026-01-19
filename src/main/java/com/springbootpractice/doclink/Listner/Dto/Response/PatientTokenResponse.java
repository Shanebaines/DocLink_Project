package com.springbootpractice.doclink.Listner.Dto.Response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PatientTokenResponse {
    private Long prescriptionId;
    private String token;
    private LocalDateTime expiresAt;
    private Boolean active;
}