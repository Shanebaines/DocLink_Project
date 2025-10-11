package com.springbootpractice.doclink.Listner.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springbootpractice.doclink.Kernal.Service.DoctorService;
import com.springbootpractice.doclink.Listner.Dto.Response.PagedResponse;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewDoctorDto;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewDoctorsDto;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/doctor")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174"}) // adjust if needed
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

    // Existing: paged search (name or specialization)
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ViewDoctorsDto>> searchDoctors(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String district, // ignored in current implementation
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return doctorService.searchDoctors(q, specialization, district, page, size);
    }

    // Existing: non-paged search (returns same shape as /viewAll)
    @GetMapping("/searchList")
    public ResponseEntity<List<ViewDoctorsDto>> searchDoctorsList(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String specialization) {
        return doctorService.searchDoctorsList(q, specialization);
    }

    // NEW: paged search + hospital filters
    @GetMapping("/searchByHospital")
    public ResponseEntity<PagedResponse<ViewDoctorsDto>> searchDoctorsByHospital(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) Long hospitalId,
            @RequestParam(required = false, name = "hospital") String hospitalName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return doctorService.searchDoctorsByHospital(q, specialization, hospitalId, hospitalName, page, size);
    }

    // NEW: non-paged version (same shape as /viewAll)
    @GetMapping("/searchByHospitalList")
    public ResponseEntity<List<ViewDoctorsDto>> searchDoctorsByHospitalList(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) Long hospitalId,
            @RequestParam(required = false, name = "hospital") String hospitalName) {
        return doctorService.searchDoctorsByHospitalList(q, specialization, hospitalId, hospitalName);
    }

    @GetMapping("/specializations")
    public ResponseEntity<List<String>> specializations() {
        return doctorService.getSpecializations();
    }
}