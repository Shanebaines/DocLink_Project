package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Kernal.Service.DoctorPageService;
import com.springbootpractice.doclink.Listner.Dto.Response.DoctorSlotOverviewDto;
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

    @GetMapping("/slot/{slotId}/details")
    public ResponseEntity<DoctorSlotOverviewDto> getSlotDetails(
            @PathVariable Long slotId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(doctorPageService.getSlotDetailsWithSeats(slotId, date));
    }
}