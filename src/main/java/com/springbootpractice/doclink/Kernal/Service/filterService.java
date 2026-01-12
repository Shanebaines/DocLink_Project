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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class filterService {

    private final DoctorService doctorService;

    public ResponseEntity<List<WorkPLaceDto>> viewTodayWorkPlaces(Long doctorId) {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        return viewWorkPlacesByDay(doctorId, today);
    }

    public ResponseEntity<List<WorkPLaceDto>> viewWorkPlacesByDay(Long doctorId, DayOfWeek dayOfWeek) {
        List<WorkPLaceDto> filteredWorkPlaces = findWorkPlacesByDay(doctorId, dayOfWeek);
        return ResponseEntity.ok(filteredWorkPlaces);
    }

    public ResponseEntity<List<WorkPLaceDto>> viewWorkPlaceNow(Long doctorId) {
        List<WorkPLaceDto> todayWorkPlaces = viewTodayWorkPlaces(doctorId).getBody();
        if (todayWorkPlaces == null || todayWorkPlaces.isEmpty()) {
            log.info("No workplaces found for today for doctor id: {}", doctorId);
            return ResponseEntity.ok(Collections.emptyList());
        }

        LocalTime now = LocalTime.now();

        // Flatten all slots with their parent workplace
        List<WorkPlaceSlotWrapper> allSlots = todayWorkPlaces.stream()
                .flatMap(workPlace -> workPlace.getAvailableSlots().stream()
                        .map(slot -> new WorkPlaceSlotWrapper(workPlace, slot)))
                .collect(Collectors.toList());

        // Parse timePeriod into start/end
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        List<WorkPlaceSlotWrapper> sortedByStartTime = allSlots.stream()
                .peek(wrapper -> {
                    String[] parts = wrapper.slot.getTimePeriod().split(" - ");
                    wrapper.start = LocalTime.parse(parts[0].trim(), fmt);
                    wrapper.end = LocalTime.parse(parts[1].trim(), fmt);
                })
                .sorted((a, b) -> a.start.compareTo(b.start))
                .collect(Collectors.toList());

        // Find the "latest relevant" — either current ongoing, or next upcoming.
        WorkPlaceSlotWrapper selected = sortedByStartTime.stream()
                .filter(wrapper -> !wrapper.end.isBefore(now))
                .findFirst()
                .orElse(null);

        if (selected == null) {
            log.info("No upcoming slots found today for doctor id: {}", doctorId);
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Return that workplace only (you can wrap as list for consistency)
        return ResponseEntity.ok(List.of(selected.workPlace));
    }

    private static class WorkPlaceSlotWrapper {
        WorkPLaceDto workPlace;
        AvailableSlotsDto slot;
        LocalTime start;
        LocalTime end;

        WorkPlaceSlotWrapper(WorkPLaceDto wp, AvailableSlotsDto s) {
            this.workPlace = wp;
            this.slot = s;
        }
    }

    private List<WorkPLaceDto> findWorkPlacesByDay(Long doctorId, DayOfWeek dayOfWeek) {
        // Step 1: Fetch workplace list
        List<WorkPLaceDto> allWorkPlaces = doctorService.viewWorkPlaces(doctorId).getBody();

        if (allWorkPlaces == null || allWorkPlaces.isEmpty()) {
            log.info("No workplaces found for doctor with id: {}", doctorId);
            return Collections.emptyList();
        }

        // Step 2: Map each workplace → only include slots of target day
        return allWorkPlaces.stream()
                .map(workPlace -> filterWorkPlaceSlots(workPlace, dayOfWeek))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private WorkPLaceDto filterWorkPlaceSlots(WorkPLaceDto workPlace, DayOfWeek dayOfWeek) {
        List<AvailableSlotsDto> matchingSlots = workPlace.getAvailableSlots().stream()
                .filter(slot -> slot.getDayOfWeek() == dayOfWeek)
                .collect(Collectors.toList());

        if (matchingSlots.isEmpty()) {
            return null; // no slots that day, exclude workplace
        }

        // Build a new DTO to avoid mutability issues
        WorkPLaceDto filtered = new WorkPLaceDto();
        filtered.setHospitalId(workPlace.getHospitalId());
        filtered.setHospitalName(workPlace.getHospitalName());
        filtered.setGpsLocation(workPlace.getGpsLocation());
        filtered.setHospitalAddress(workPlace.getHospitalAddress());
        filtered.setPhoneNumber(workPlace.getPhoneNumber());
        filtered.setAvailableSlots(matchingSlots);

        return filtered;
    }
}