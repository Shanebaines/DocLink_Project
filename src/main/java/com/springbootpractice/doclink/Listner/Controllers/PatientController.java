package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Kernal.Service.PatientService;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewPatientDto;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewPatientsDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient")
@AllArgsConstructor
public class PatientController {
    public final PatientService patientService;

    @GetMapping("/viewPatient/{id}")
    public ResponseEntity<ViewPatientDto> viewPatient(@PathVariable Long id) {
        return patientService.viewPatient(id);
    }


    @GetMapping("/viewAll")
    public ResponseEntity<List<ViewPatientsDto>> viewPatients() {
        return patientService.viewPatients();
    }
}
