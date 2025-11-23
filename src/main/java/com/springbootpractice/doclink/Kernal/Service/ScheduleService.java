package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.AppointmentRepository;
import com.springbootpractice.doclink.Dealer.DoctorTimeSlotRepository;
import com.springbootpractice.doclink.Kernal.Entity.Appointment;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_time_slots;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewSlotDto;
import com.springbootpractice.doclink.Listner.Dto.Response.seatDto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorTimeSlotRepository doctorTimeSlotRepository;

    /**
     * Unified method to view a slot.
     * Uses 'isPatientView' to determine which specific fields to populate.
     */
    public ResponseEntity<ViewSlotDto> viewSlot(Long slotId, LocalDate date, boolean isPatientView) {
        Doctor_time_slots timeSlot = doctorTimeSlotRepository.findById(slotId).orElse(null);
        if (timeSlot == null) {
            return ResponseEntity.notFound().build();
        }

        // 1. Fetch all appointments for this slot/date and map them by Seat Number
        // Using 'findAllByTimeSlot_IdAndAppointmentDate' (with underscore) to match your Repository
        Map<Integer, Appointment> bookedAppointments = appointmentRepository.findAllByTimeSlot_IdAndAppointmentDate(slotId, date)
                .stream()
                .collect(Collectors.toMap(Appointment::getSeatNumber, Function.identity()));

        // 2. Build the list of ALL seats (1 to Total)
        List<seatDto> allSeats = IntStream.rangeClosed(1, timeSlot.getTotalSeats())
                .mapToObj(seatNum -> {
                    seatDto seat = new seatDto();
                    seat.setSeatNumber(seatNum);

                    if (bookedAppointments.containsKey(seatNum)) {
                        // If booked, use the REAL status (scheduled, completed, no_show)
                        seat.setStatus(bookedAppointments.get(seatNum).getStatus());
                    } else {
                        // If not booked, status is null (Available)
                        seat.setStatus(null);
                    }
                    return seat;
                })
                .collect(Collectors.toList());

        // 3. Build the Response DTO
        ViewSlotDto dto = new ViewSlotDto();

        // -- Common Fields --
        dto.setSlotId(timeSlot.getId());
        dto.setHospitalName(timeSlot.getHospital().getHospitalName());
        dto.setTotalSeats(timeSlot.getTotalSeats());
        dto.setTimePeriod(timeSlot.getStartTime().toString() + " - " + timeSlot.getEndTime().toString());
        dto.setSeats(allSeats);

        // We force availability to TRUE as per your database structure constraints
        dto.setAvailability(true);

        // -- Conditional Fields Logic --
        if (isPatientView) {
            // PATIENT VIEW: Needs Doctor Name and Free Seat Count
            dto.setDoctorName(timeSlot.getDoctor().getUser().getFirstName() + " " + timeSlot.getDoctor().getUser().getLastName());
            dto.setDate(date);
            dto.setFreeSeats(timeSlot.getTotalSeats() - bookedAppointments.size());
            // hospitalId and dayOfWeek remain NULL (Hidden by @JsonInclude)
        } else {
            // DOCTOR VIEW: Needs Hospital ID and Day of Week
            dto.setHospitalId(timeSlot.getHospital().getHospitalId());
            dto.setDayOfWeek(timeSlot.getDayOfWeek());
            dto.setDate(date);
            // doctorName and freeSeats remain NULL (Hidden by @JsonInclude)
        }

        return ResponseEntity.ok(dto);
    }

    /**
     * Helper to get upcoming dates for the dropdown
     */
    public ResponseEntity<List<LocalDate>> upcomingDates(Long slotId) {
        Doctor_time_slots slots = doctorTimeSlotRepository.findById(slotId)
                .orElse(null);

        if (slots == null) {
            return ResponseEntity.notFound().build();
        }

        DayOfWeek dayOfWeek = slots.getDayOfWeek();
        LocalDate today = LocalDate.now();

        List<LocalDate> upcomingDates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(4 * 7) // 4 weeks
                .filter(date -> date.getDayOfWeek() == dayOfWeek)
                .collect(Collectors.toList());

        return ResponseEntity.ok(upcomingDates);
    }
}