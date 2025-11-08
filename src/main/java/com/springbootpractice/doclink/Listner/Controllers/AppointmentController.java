package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Kernal.Entity.Appointment;
import com.springbootpractice.doclink.Kernal.Service.AppointmentService;
import com.springbootpractice.doclink.Listner.Dto.Request.CreateAppointmentRequestDto;
import com.springbootpractice.doclink.Listner.Dto.Request.patientSeatDto;
import com.springbootpractice.doclink.Listner.Dto.Request.UpdateStatusRequestDto;
import com.springbootpractice.doclink.Listner.Dto.Response.seatViewDto;
import com.springbootpractice.doclink.Listner.Dto.Response.viewAppointmentsDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("appointment")
@AllArgsConstructor
public class AppointmentController {

    public final AppointmentService appointmentService;

    @GetMapping("/viewMyAppointments") //tested
    public ResponseEntity<List<viewAppointmentsDto>> viewAppointments(@RequestParam Long id){
        return appointmentService.viewAppointments(id);
    }

    @GetMapping("/viewSeat")
    public ResponseEntity<seatViewDto> viewSeat(@RequestBody patientSeatDto patientSeatDto){
        return appointmentService.viewSeat(patientSeatDto);
    }

    @PostMapping("/book") //tested
    public ResponseEntity<?> bookAppointment(@Valid @RequestBody CreateAppointmentRequestDto requestDto) {
        try {
            Appointment savedAppointment = appointmentService.createAppointment(requestDto);
            return new ResponseEntity<>("Appointment booked successfully with ID: " + savedAppointment.getAppointmentId(), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Catches errors like "Patient not found"
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            // Catches "Seat is already taken"
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected server error occurred.");
        }
    }

    @PatchMapping("/updateStatus") //admin API
    public ResponseEntity<?> updateStatus(@RequestParam Long id, @Valid @RequestBody UpdateStatusRequestDto requestDto) {
        try {
            Appointment updatedAppointment = appointmentService.updateAppointmentStatus(id, requestDto);
            return ResponseEntity.ok("Status for appointment " + id + " updated to " + updatedAppointment.getStatus());
        } catch (IllegalArgumentException e) {
            // Catches "Appointment not found"
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            // Catches "Cannot update a completed appointment"
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected server error occurred.");
        }
    }

    @PatchMapping("/updateStatusByDoctor")
    public ResponseEntity<?> updateAppointmentStatusByDoctor(@Valid @RequestBody patientSeatDto requestDto) {
        try {
            Appointment updated = appointmentService.updateAppointmentStatusByDoctor(requestDto);
            String msg = String.format("Appointment %d marked as COMPLETED successfully.", updated.getAppointmentId());
            return ResponseEntity.ok(msg);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected server error while updating appointment status.");
        }
    }

}