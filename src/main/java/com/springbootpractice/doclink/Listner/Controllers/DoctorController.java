package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Kernal.Service.DoctorService;
import com.springbootpractice.doclink.Listner.Dto.Response.PagedResponse;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewDoctorDto;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewDoctorsDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}) // adjust if needed
public class DoctorController {
    public final DoctorService doctorService;

    @GetMapping("/view")
    public ResponseEntity<ViewDoctorDto> viewDoctor(@RequestParam Long id) {
        return doctorService.viewDoctor(id);
    }

    @GetMapping("/viewAll")
    public ResponseEntity<List<ViewDoctorsDto>> viewDoctors() {
        return doctorService.viewDoctors();
    }

    @GetMapping("/viewByHospital")
    public ResponseEntity<List<ViewDoctorsDto>> getDoctorsByHospital(@RequestParam Long hospitalId) {
        return ResponseEntity.ok(doctorService.getDoctorsByHospital(hospitalId));
    }

}