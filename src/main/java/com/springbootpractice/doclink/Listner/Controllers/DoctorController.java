package com.springbootpractice.doclink.Listner.Controllers;

import java.util.List;
import com.springbootpractice.doclink.Kernel.Service.FeedbackService;
import com.springbootpractice.doclink.Listner.Dto.Response.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.springbootpractice.doclink.Listner.Dto.Response.FeedbackViewDto;

import com.springbootpractice.doclink.Kernel.Service.DoctorService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/doctor")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174"}) // adjust if needed
public class DoctorController {
    public final DoctorService doctorService;
    public final FeedbackService feedbackService;

    @GetMapping("/view") //tested
    public ResponseEntity<ViewDoctorDto> viewDoctor(@RequestParam Integer id) {
        return doctorService.viewDoctor(Long.valueOf(id));
    }
    @GetMapping("/viewAll") //tested
    public ResponseEntity<List<ViewDoctorsDto>> viewDoctors() {
        return doctorService.viewDoctors();
    }

    @GetMapping("/viewPlaces")
    public ResponseEntity<List<WorkPLaceDto>> viewWorkPlaces(@RequestParam Long id) {
        return doctorService.viewWorkPlaces(id);
    }

    // Existing: paged search (name or specialization)
    @GetMapping("/search") //not done
    public ResponseEntity<PagedResponse<ViewDoctorsDto>> searchDoctors(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String district, // ignored in current implementation
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return doctorService.searchDoctors(q, specialization, district, page, size);
    }

    // Existing: non-paged search (returns same shape as /viewAll)
    @GetMapping("/searchList") //not done
    public ResponseEntity<List<ViewDoctorsDto>> searchDoctorsList(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String specialization) {
        return doctorService.searchDoctorsList(q, specialization);
    }

    // NEW: paged search + hospital filters
    @GetMapping("/searchByHospital") //not done
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

    @GetMapping("/{id}/feedbacks")
    public ResponseEntity<List<FeedbackViewDto>> getDoctorFeedbacks(@PathVariable Long id) {
        // Reuse the logic that hides patient names if anonymous
        return feedbackService.getDoctorFeedback(id);
    }

}