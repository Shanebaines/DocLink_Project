package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Kernal.Service.PatientService;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewPatientDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patient")
@AllArgsConstructor
public class PatientController {
    public final PatientService patientService;

    @GetMapping("/viewPatient")
    public ResponseEntity<ViewPatientDto> viewPatient(@RequestParam Long id) {
        return patientService.viewPatient(id);
    }
}
