package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Listner.Dto.Request.CreateMedicalRecordDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/medicalRecord")
@AllArgsConstructor
public class MedicalRecordController {
    ResponseEntity<?> addMedicalRecord(CreateMedicalRecordDto createMedicalRecordDto) {
        return null;
    }
}
