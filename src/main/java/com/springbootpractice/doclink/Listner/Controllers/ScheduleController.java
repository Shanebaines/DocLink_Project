package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Kernal.Service.ScheduleService;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewSlotDto;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/schedule")
@AllArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("/{slotId}/dates") //tested
    public ResponseEntity<List<LocalDate>> upcomingDates(@PathVariable Long slotId) {
        return scheduleService.upcomingDates(slotId);
    }

    @GetMapping("/{slotId}/viewSlot") //tested
    public ResponseEntity<ViewSlotDto> viewSlot(
            @PathVariable Long slotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date) {
                return scheduleService.viewSlot(slotId, date);
    }
}
