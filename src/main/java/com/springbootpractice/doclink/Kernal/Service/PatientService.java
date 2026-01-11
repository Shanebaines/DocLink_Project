package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.PatientRepository;
import com.springbootpractice.doclink.Kernal.Entity.Patient;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewPatientDto;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewPatientsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PatientService {
    public final PatientRepository patientRepository;

    public ResponseEntity<ViewPatientDto> viewPatient(Long patientId) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        if (optionalPatient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Patient patient = optionalPatient.get();
        Long userId = patient.getUser().getUserId();

        ViewPatientDto dto = new ViewPatientDto();
        dto.setPatientId(patientId);
        dto.setUserId(userId);
        dto.setGpsLocation(patient.getUser().getGpsLocation());
        dto.setPatientName(patient.getUser().getFirstName() + " " + patient.getUser().getLastName());
        dto.setGender(patient.getUser().getGender());
        dto.setEmail(patient.getUser().getEmail());
        dto.setPhoneNumber(patient.getUser().getPhoneNumber());

        return ResponseEntity.ok(dto);
    }


    public ResponseEntity<List<ViewPatientsDto>> viewPatients() {
        List<Patient> patients = patientRepository.findAll();

        if (patients.isEmpty()) {
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
                .toList();

        return ResponseEntity.ok(patientDtos);
    }

}
