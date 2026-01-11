package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.MedicalRecordRepository;
import com.springbootpractice.doclink.Listner.Dto.Request.CreateAppointmentRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MedicalRecordService {
    public final MedicalRecordRepository medicalRecordRepository;

//    public ResponseEntity<?> addMedicalRecord(CreateAppointmentRequestDto createAppointmentRequestDto) {
//    }
}
