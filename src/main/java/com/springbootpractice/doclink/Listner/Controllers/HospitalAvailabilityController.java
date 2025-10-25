package com.springbootpractice.doclink.Listner.Controllers;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springbootpractice.doclink.Kernal.Service.HospitalAvailabilityService;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@RequestMapping("/api/hospital")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174"})
public class HospitalAvailabilityController {
    private final HospitalAvailabilityService availabilityService;

    @PutMapping("/update-doctor-availability")
    public ResponseEntity<?> updateDoctorAvailability(@RequestBody UpdateAvailabilityRequest req) {
        LocalDate date = req.getDate();
    availabilityService.updateAvailability(req.getDoctorId(), req.getHospitalId(), date, req.getStatus());

        boolean coming = "coming".equalsIgnoreCase(req.getStatus());

    return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Doctor availability updated successfully.",
                "data", Map.of(
                        "doctorId", req.getDoctorId(),
            "hospital_id", req.getHospitalId(),
            "date", date.toString(),
                        "coming", coming
                )
        ));
    }

    @Data
    public static class UpdateAvailabilityRequest {
        private Long doctorId;
        @com.fasterxml.jackson.annotation.JsonProperty("hospital_id")
        private Long hospitalId;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate date;
        private String status; // "coming" or "not_coming"
    }
}
