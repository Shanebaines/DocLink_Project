package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Kernal.Service.MedicalRecordService;
import com.springbootpractice.doclink.Listner.Dto.Request.CreateMedicalRecordDto;
import com.springbootpractice.doclink.Listner.Dto.Request.CreatePrescriptionRequest;
import com.springbootpractice.doclink.Listner.Dto.Response.MedicalRecordDto;
import com.springbootpractice.doclink.Listner.Dto.Response.PrescriptionResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/medicalRecord")
@AllArgsConstructor
public class MedicalRecordController {
    public final  MedicalRecordService medicalRecordService;

    @PostMapping("/createMedicalRecord")
    public ResponseEntity<MedicalRecordDto> createMedicalRecord(
            @RequestBody CreateMedicalRecordDto createDto) {
        return medicalRecordService.createMedicalRecord(createDto);
    }

    @PostMapping("/createPrescription")
    public ResponseEntity<PrescriptionResponse> createPrescription(
            @RequestBody @Valid CreatePrescriptionRequest request) {
        return medicalRecordService.createPrescription(request);
    }
}
