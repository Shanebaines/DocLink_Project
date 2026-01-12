package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Kernal.Service.MedicalRecordService;
import com.springbootpractice.doclink.Listner.Dto.Request.*;
import com.springbootpractice.doclink.Listner.Dto.Response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicalRecords")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    /**
     * Create a complete medical record with optional report and/or prescription
     * Use this when creating both or either in a single request
     */
    @PostMapping
    public ResponseEntity<MedicalRecordResponseDto> createMedicalRecord(
            @RequestBody @Valid CreateMedicalRecordDto request) {
        return medicalRecordService.createMedicalRecord(request);
    }

    /**
     * Create medical record with only a medical report
     */
    @PostMapping("/report")
    public ResponseEntity<MedicalRecordResponseDto> createMedicalReportOnly(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestBody @Valid CreateMedicalReportDto reportDto) {
        return medicalRecordService.createMedicalReportOnly(patientId, doctorId, reportDto);
    }

    /**
     * Create medical record with only a prescription
     */
    @PostMapping("/prescription")
    public ResponseEntity<MedicalRecordResponseDto> createPrescriptionOnly(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestBody @Valid CreatePrescriptionDto prescriptionDto) {
        return medicalRecordService.createPrescriptionOnly(patientId, doctorId, prescriptionDto);
    }

    /**
     * Get medical record by ID with all details
     */
    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDto> getMedicalRecordById(
            @PathVariable Long id) {
        return medicalRecordService.getMedicalRecordById(id);
    }

    /**
     * Get all medical records for a patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecordResponseDto>> getMedicalRecordsByPatientId(
            @PathVariable Long patientId) {
        return medicalRecordService.getMedicalRecordsByPatientId(patientId);
    }
}