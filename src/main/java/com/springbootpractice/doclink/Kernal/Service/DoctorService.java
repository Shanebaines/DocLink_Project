package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.AppointmentRepository;
import com.springbootpractice.doclink.Dealer.DoctorAvailabilityRepository;
import com.springbootpractice.doclink.Dealer.DoctorRepository;
import com.springbootpractice.doclink.Dealer.DoctorsInHospitalRepository;
import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import com.springbootpractice.doclink.Kernal.Entity.Hospital;
import com.springbootpractice.doclink.Kernal.Enums.AppointmentStatusType;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_availability;
import com.springbootpractice.doclink.Kernal.Relations.Doctors_in_Hospital;
import com.springbootpractice.doclink.Listner.Dto.Response.AvailableSlotsDto;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewDoctorDto;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewDoctorsDto;
import com.springbootpractice.doclink.Listner.Dto.Response.WorkPLaceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorsInHospitalRepository doctorsInHospitalRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final AppointmentRepository appointmentRepository;

    public ResponseEntity<ViewDoctorDto> viewDoctor(Long id) {
        Optional<Doctor> optDoctor = doctorRepository.findById(Math.toIntExact(id));
        if (optDoctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Doctor doctor = optDoctor.get();

        ViewDoctorDto dto = new ViewDoctorDto();
        dto.setImage(doctor.getImage());
        dto.setDoctorName(doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        dto.setYearOfExperience(doctor.getYearsExperience());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setQualification(doctor.getQualification());
        dto.setPhoneNumber(doctor.getUser().getPhoneNumber());
        dto.setEmail(doctor.getUser().getEmail());
        dto.setAddress(doctor.getUser().getAddress());

        List<Doctors_in_Hospital> places = doctorsInHospitalRepository.findAllByDoctorIdWithHospital(id);

        List<WorkPLaceDto> workPlaces = places.stream()
                .map(dih -> toWorkPlaceDto(dih, id))
                .collect(Collectors.toList());

        dto.setWorkPlaces(workPlaces);

        return ResponseEntity.ok(dto);
    }

    private WorkPLaceDto toWorkPlaceDto(Doctors_in_Hospital dih, Long doctorId) {
        Hospital h = dih.getHospital();

        WorkPLaceDto w = new WorkPLaceDto();
        w.setHospitalId(h.getHospitalId());
        w.setHospitalName(h.getHospitalName());
        w.setGpsLocation(h.getGpsLocation());
        w.setHospitalAddress(h.getAddress());
        w.setPhoneNumber(h.getPhoneNumber());

        // Load all active availability rows for this doctor at this hospital
        List<Doctor_availability> slots = doctorAvailabilityRepository
                .findByDoctorDoctorIdAndHospitalHospitalIdAndAvailabilityTrueOrderByDayOfWeekAscStartTimeAsc(
                        doctorId, h.getHospitalId()
                );

        List<AvailableSlotsDto> availableSlots = slots.stream()
                .map(this::toAvailableSlotDto)
                .collect(Collectors.toList());

        w.setAvailableSlots(availableSlots);
        return w;
    }

    private AvailableSlotsDto toAvailableSlotDto(Doctor_availability slot) {
        AvailableSlotsDto a = new AvailableSlotsDto();
        a.setTotalSeats(slot.getTotalSeats());

        // Resolve the next upcoming occurrence for this weekly slot
        SlotWindow window = resolveNextWindow(slot);
        String periodLabel = formatTimePeriod(
                slot.getDayOfWeek(),
                slot.getStartTime(),
                slot.getEndTime(),
                window.start()
        );
        a.setTimePeriod(periodLabel);

        // If the slot is marked unavailable or outside effective period, mark unavailable
        if (!Boolean.TRUE.equals(slot.getAvailability()) || !isWithinEffectivePeriod(slot, window)) {
            a.setAvailability("UNAVAILABLE");
            a.setAvailableSeats(0);
            return a;
        }

        // Count how many appointments are booked within that window (handles overnight)
        int booked = countBookedForWindow(slot, window.start(), window.end());

        int remaining = Math.max(slot.getTotalSeats() - booked, 0);
        a.setAvailableSeats(remaining);
        a.setAvailability(remaining > 0 ? "AVAILABLE" : "FULL");
        return a;
    }

    // Represents a concrete date window for the next occurrence of a weekly slot
    private record SlotWindow(LocalDateTime start, LocalDateTime end) {}

    private SlotWindow resolveNextWindow(Doctor_availability slot) {
        LocalDate nowDate = LocalDate.now();
        DayOfWeek targetDow = slot.getDayOfWeek();

        // Find the next date for that DayOfWeek (today or later)
        int daysUntil = (targetDow.getValue() - nowDate.getDayOfWeek().getValue() + 7) % 7;
        LocalDate candidateDate = nowDate.plusDays(daysUntil);

        LocalDateTime start = LocalDateTime.of(candidateDate, slot.getStartTime());
        LocalDateTime end = LocalDateTime.of(candidateDate, slot.getEndTime());

        // Handle overnight ranges (end before start)
        if (slot.getEndTime().isBefore(slot.getStartTime())) {
            end = end.plusDays(1);
        }

        // If it's today and the window already ended, jump to next week
        if (daysUntil == 0 && LocalDateTime.now().isAfter(end)) {
            candidateDate = candidateDate.plusWeeks(1);
            start = LocalDateTime.of(candidateDate, slot.getStartTime());
            end = LocalDateTime.of(candidateDate, slot.getEndTime());
            if (slot.getEndTime().isBefore(slot.getStartTime())) {
                end = end.plusDays(1);
            }
        }

        return new SlotWindow(start, end);
    }

    private boolean isWithinEffectivePeriod(Doctor_availability slot, SlotWindow window) {
        LocalDateTime from = slot.getEffectiveFrom();
        LocalDateTime until = slot.getEffectiveUntil();
        if (from != null && window.end().isBefore(from)) return false;
        if (until != null && window.start().isAfter(until)) return false;
        return true;
    }

    private String formatTimePeriod(DayOfWeek dow, LocalTime start, LocalTime end, LocalDateTime occurrenceDateTime) {
        DateTimeFormatter fmtTime = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter fmtDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dowShort = dow.name().substring(0, 3);
        return String.format("%s (%s) %s - %s",
                occurrenceDateTime.toLocalDate().format(fmtDate),
                dowShort,
                start.format(fmtTime),
                end.format(fmtTime)
        );
    }

    // Which statuses should block seats (adjust to match your enum values)
    private List<AppointmentStatusType> activeStatuses() {
        // Add more if needed: e.g., confirmed
        return List.of(AppointmentStatusType.scheduled);
    }

    // Count appointments for the resolved window (may span midnight)
    private int countBookedForWindow(Doctor_availability slot, LocalDateTime windowStart, LocalDateTime windowEnd) {
        Long doctorId = slot.getDoctor().getDoctorId();
        Long hospitalId = slot.getHospital().getHospitalId();

        LocalDate startDate = windowStart.toLocalDate();
        LocalDate endDate = windowEnd.toLocalDate();
        LocalTime startTime = slot.getStartTime();
        LocalTime endTime = slot.getEndTime();
        List<AppointmentStatusType> statuses = activeStatuses();

        if (startDate.equals(endDate)) {
            // Same date: [startTime, endTime)
            return appointmentRepository.countOnDateBetweenTimes(
                    doctorId, hospitalId, startDate, startTime, endTime, statuses
            );
        } else {
            // Overnight to next day: split into two counts
            int firstPart = appointmentRepository.countOnDateFromTime(
                    doctorId, hospitalId, startDate, startTime, statuses
            );
            int secondPart = appointmentRepository.countOnDateBeforeTime(
                    doctorId, hospitalId, endDate, endTime, statuses
            );
            return firstPart + secondPart;
        }
    }

    public ResponseEntity<List<ViewDoctorsDto>> viewDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<ViewDoctorsDto> doctorsAvailable = doctors.stream()
                .map(this::toViewDoctorsDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctorsAvailable);
    }

    private ViewDoctorsDto toViewDoctorsDto(Doctor doctor) {
        ViewDoctorsDto dto = new ViewDoctorsDto();
        dto.setImage(doctor.getImage());
        dto.setDoctorId(doctor.getDoctorId());
        dto.setName(doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName());
        dto.setSpecialization(doctor.getSpecialization());
        return dto;
    }
}