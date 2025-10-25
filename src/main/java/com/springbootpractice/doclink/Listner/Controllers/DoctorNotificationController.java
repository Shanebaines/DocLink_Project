package com.springbootpractice.doclink.Listner.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springbootpractice.doclink.Kernal.Service.NotificationService;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@RequestMapping("/api/doctor")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174"})
public class DoctorNotificationController {
    private final NotificationService notificationService;

    @PostMapping("/{doctorId}/notify-availability")
    public ResponseEntity<?> notifyAvailability(@PathVariable Long doctorId, @RequestBody NotifyRequest req) {
        // basic validation is inside service
        notificationService.sendAvailabilityNotification(doctorId, req.getHospitalId(), req.getMessage(), req.getStatus());
        return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "message", "Notification sent to hospital successfully."
        ));
    }

    @Data
    public static class NotifyRequest {
        @com.fasterxml.jackson.annotation.JsonProperty("hospital_id")
        private Long hospitalId;
        private String message;
        private String status; // "coming" or "not_coming"
    }
}
