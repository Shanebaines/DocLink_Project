package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Kernal.Service.DoctorPageService;
import com.springbootpractice.doclink.Kernal.Service.ScheduleService;
import com.springbootpractice.doclink.Listner.Dto.Response.DoctorAppointmentDetailDto;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewSlotDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/doctor-dashboard")
@RequiredArgsConstructor
public class DoctorPageController {

    private final DoctorPageService doctorPageService;
    private final ScheduleService scheduleService;

    // 1. THE GRID VIEW
    @GetMapping("/slot/{slotId}/details")
    public ResponseEntity<ViewSlotDto> getSlotDetails(
            @PathVariable Long slotId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // === FIX: Pass 'false' as the 3rd argument ===
        // 'false' = This is the Doctor View (Hide Doctor Name, Show Hospital ID)
        return scheduleService.viewSlot(slotId, date, false);
    }

    // 2. THE SEAT DETAILS (Private Info)
    @GetMapping("/slot/{slotId}/seat/{seatNumber}")
    public ResponseEntity<DoctorAppointmentDetailDto> getSeatDetails(
            @PathVariable Long slotId,
            @PathVariable Integer seatNumber,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(doctorPageService.getAppointmentDetailsBySeatCoordinates(slotId, date, seatNumber));
    }
}