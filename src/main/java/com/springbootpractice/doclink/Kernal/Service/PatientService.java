package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.PatientRepository;
import com.springbootpractice.doclink.Kernal.Entity.Patient;
import com.springbootpractice.doclink.Kernal.Relations.PrescriptionToken;
import com.springbootpractice.doclink.Dealer.PrescriptionTokenRepository;
import com.springbootpractice.doclink.Listner.Dto.Response.PatientTokenResponse;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewPatientDto;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewPatientsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final PrescriptionTokenRepository tokenRepository;

    /**
     * Returns a specific patient's detailed information
     */
    public ResponseEntity<ViewPatientDto> viewPatient(Long patientId) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        if (optionalPatient.isEmpty()) {
            log.warn("Patient not found with ID {}", patientId);
            return ResponseEntity.notFound().build();
        }

        Patient patient = optionalPatient.get();

        ViewPatientDto dto = new ViewPatientDto();
        dto.setPatientId(patient.getPatientId());
        dto.setUserId(patient.getUser().getUserId());
        dto.setGpsLocation(patient.getUser().getGpsLocation());
        dto.setPatientName(patient.getUser().getFirstName() + " " + patient.getUser().getLastName());
        dto.setGender(patient.getUser().getGender());
        dto.setEmail(patient.getUser().getEmail());
        dto.setPhoneNumber(patient.getUser().getPhoneNumber());

        return ResponseEntity.ok(dto);
    }

    /**
     * Returns a simple list of all patients
     */
    public ResponseEntity<List<ViewPatientsDto>> viewPatients() {
        List<Patient> patients = patientRepository.findAll();

        if (patients.isEmpty()) {
            log.info("No patients found in database");
            return ResponseEntity.noContent().build();
        }

        List<ViewPatientsDto> patientDtos = patients.stream()
                .map(patient -> {
                    ViewPatientsDto dto = new ViewPatientsDto();
                    dto.setPatientId(patient.getPatientId());
                    dto.setUserId(patient.getUser().getUserId());
                    dto.setFirstName(patient.getUser().getFirstName());
                    dto.setLastName(patient.getUser().getLastName());
                    dto.setAddress(patient.getUser().getAddress());
                    dto.setGpsLocation(patient.getUser().getGpsLocation());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(patientDtos);
    }

    /**
     * Returns all tokens for prescriptions associated with a patient
     */
    public ResponseEntity<List<PatientTokenResponse>> getPatientTokens(Long patientId) {
        Optional<Patient> optPatient = patientRepository.findById(patientId);
        if (optPatient.isEmpty()) {
            log.warn("Patient for token view not found with ID {}", patientId);
            return ResponseEntity.notFound().build();
        }

        List<PrescriptionToken> tokens =
                tokenRepository.findByPrescription_MedicalRecord_Patient_PatientId(patientId);

        if (tokens.isEmpty()) {
            log.info("No tokens found for patient ID {}", patientId);
            return ResponseEntity.noContent().build();
        }

        List<PatientTokenResponse> responseList = tokens.stream().map(token -> {
            PatientTokenResponse dto = new PatientTokenResponse();
            dto.setPrescriptionId(token.getPrescription().getPrescriptionId());
            dto.setToken(token.getToken());
            dto.setExpiresAt(token.getExpiresAt());
            dto.setActive(token.getActive());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
}