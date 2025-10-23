package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Listner.Dto.Response.AvailableSlotsDto;
import com.springbootpractice.doclink.Listner.Dto.Response.WorkPLaceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class filterService {

    private final DoctorService doctorService;

    public ResponseEntity<List<WorkPLaceDto>> viewTodayWorkPlaces(Long id) {
        // Step 1: Get doctor’s workplaces (with all slots)
        List<WorkPLaceDto> workPlaces = doctorService.viewWorkPlaces(id).getBody();
        if (workPlaces == null || workPlaces.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        // Step 2: Determine today’s day of week
        DayOfWeek today = LocalDate.now().getDayOfWeek();

        // Step 3: Filter each workplace’s slots to include only today's
        List<WorkPLaceDto> todayWorkPlaces = workPlaces.stream()
                .map(workPlace -> {
                    List<AvailableSlotsDto> todaySlots = workPlace.getAvailableSlots().stream()
                            .filter(slot -> slot.getDayOfWeek() == today)
                            .collect(Collectors.toList());
                    // Only include workplaces that have slots today
                    if (!todaySlots.isEmpty()) {
                        WorkPLaceDto filtered = new WorkPLaceDto();
                        filtered.setHospitalId(workPlace.getHospitalId());
                        filtered.setHospitalName(workPlace.getHospitalName());
                        filtered.setGpsLocation(workPlace.getGpsLocation());
                        filtered.setHospitalAddress(workPlace.getHospitalAddress());
                        filtered.setPhoneNumber(workPlace.getPhoneNumber());
                        filtered.setAvailableSlots(todaySlots);
                        return filtered;
                    } else {
                        return null;
                    }
                })
                .filter(wp -> wp != null)
                .collect(Collectors.toList());

        // Step 4: Return response
        return ResponseEntity.ok(todayWorkPlaces);
    }
}