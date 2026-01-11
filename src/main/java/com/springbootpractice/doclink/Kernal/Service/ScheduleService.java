package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.AppointmentRepository;
import com.springbootpractice.doclink.Dealer.DoctorAvailabilityRepository;
import com.springbootpractice.doclink.Dealer.DoctorTimeSlotRepository;
import com.springbootpractice.doclink.Kernal.Entity.Appointment;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_time_slots;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewSlotDto;
import com.springbootpractice.doclink.Listner.Dto.Response.seatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springbootpractice.doclink.Kernal.Enums.AppointmentStatusType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorTimeSlotRepository doctorTimeSlotRepository;

    public ResponseEntity<ViewSlotDto> viewSlot(Long slotId, LocalDate date) {
        Doctor_time_slots timeSlot = doctorTimeSlotRepository.findById(slotId).orElse(null);
        if (timeSlot == null) {
            return ResponseEntity.notFound().build();
        }

        // 1. Get ONLY the numbers of the booked seats for this slot and date
        Set<Integer> bookedSeatNumbers = appointmentRepository.findAllByTimeSlot_IdAndAppointmentDate(slotId, date)
                .stream()
                .map(Appointment::getSeatNumber)
                .collect(Collectors.toSet());

        // 2. Build a list of ALL seats (from 1 to totalSeats), marking their status
        List<seatDto> allSeats = IntStream.rangeClosed(1, timeSlot.getTotalSeats())
                .mapToObj(seatNum -> {
                    seatDto seat = new seatDto();
                    seat.setSeatNumber(seatNum);
                    // If the number is in our set of booked numbers, it's taken. Otherwise, it's available.
                    if (bookedSeatNumbers.contains(seatNum)) {
                        seat.setStatus(AppointmentStatusType.scheduled); // You can use "scheduled" to mean "booked"
                    } else {
                        // For available seats, you can set status to null or create an "AVAILABLE" enum value
                        seat.setStatus(null);
                    }
                    return seat;
                })
                .collect(Collectors.toList());


        ViewSlotDto viewSlotDto = new ViewSlotDto();
        viewSlotDto.setDoctorName(timeSlot.getDoctor().getUser().getFirstName() + " " + timeSlot.getDoctor().getUser().getLastName());
        viewSlotDto.setHospitalName(timeSlot.getHospital().getHospitalName());
        viewSlotDto.setDate(date);

        // Use the corrected repository method
        Boolean availability = doctorAvailabilityRepository.getAvailabilityBySlotId(slotId);
        viewSlotDto.setAvailability(availability != null && availability);

        viewSlotDto.setFreeSeats(timeSlot.getTotalSeats() - bookedSeatNumbers.size());
        viewSlotDto.setTotalSeats(timeSlot.getTotalSeats());
        viewSlotDto.setTimePeriod(timeSlot.getStartTime().toString() + " - " + timeSlot.getEndTime().toString());
        viewSlotDto.setSeats(allSeats); // Set the complete list of all seats

        return ResponseEntity.ok(viewSlotDto);
    }

    // This private helper method is no longer needed because the logic is now inside viewSlot
    // private seatsDto toSeatsDto(Appointment appointment) { ... }


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