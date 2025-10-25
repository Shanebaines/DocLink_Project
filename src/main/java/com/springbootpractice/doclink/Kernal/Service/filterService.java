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

    /**
     * Returns all doctor's workplaces with slots available today.
     */
    public ResponseEntity<List<WorkPLaceDto>> viewTodayWorkPlaces(Long doctorId) {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        return viewWorkPlacesByDay(doctorId, today);
    }

    /**
     * Returns all doctor's workplaces filtered by a specific weekday.
     */
    public ResponseEntity<List<WorkPLaceDto>> viewWorkPlacesByDay(Long doctorId, DayOfWeek dayOfWeek) {
        List<WorkPLaceDto> filteredWorkPlaces = findWorkPlacesByDay(doctorId, dayOfWeek);
        return ResponseEntity.ok(filteredWorkPlaces);
    }

    /**
     * Retrieves and filters the doctor’s workplaces to include only those
     * that have available slots on the given day.
     */
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

    /**
     * Filters a workplace’s available slots to only include the specified day.
     */
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