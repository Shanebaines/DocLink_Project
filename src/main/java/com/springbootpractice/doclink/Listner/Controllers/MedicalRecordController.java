package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Kernal.Service.MedicalRecordService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/medicalRecord")
@AllArgsConstructor
public class MedicalRecordController {
    public final MedicalRecordService medicalRecordService;

//    @PostMapping("/addRecord")
//    ResponseEntity<?> addMedicalRecord(CreateAppointmentRequestDto createAppointmentRequestDto) {
//        return medicalRecordService.addMedicalRecord(createAppointmentRequestDto);
//    }
}
