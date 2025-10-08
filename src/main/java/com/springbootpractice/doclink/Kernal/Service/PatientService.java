package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.PatientRepository;
import com.springbootpractice.doclink.Kernal.Entity.Patient;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewPatientDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
}
