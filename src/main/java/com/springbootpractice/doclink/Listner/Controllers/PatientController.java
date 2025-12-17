package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Kernel.Service.PatientService;
import com.springbootpractice.doclink.Listner.Dto.Response.PatientTokenResponse;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewPatientDto;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewPatientsDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient")
@AllArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;

    /**
     * View details for a single patient
     */
    @GetMapping("/viewPatient")
    public ResponseEntity<ViewPatientDto> viewPatient(@RequestParam Long id) {
        log.info("Request to view patient with ID {}", id);
        return patientService.viewPatient(id);
    }

    /**
     * View all patients
     */
    @GetMapping("/viewAll")
    public ResponseEntity<List<ViewPatientsDto>> viewPatients() {
        log.info("Request to list all patients");
        return patientService.viewPatients();
    }

    /**
     * View all tokens associated with a specific patient
     */
    @GetMapping("/tokens/{patientId}")
    public ResponseEntity<List<PatientTokenResponse>> getPatientTokens(
            @PathVariable Long patientId) {
        log.info("Request to view tokens for patient ID {}", patientId);
        return patientService.getPatientTokens(patientId);
    }
}