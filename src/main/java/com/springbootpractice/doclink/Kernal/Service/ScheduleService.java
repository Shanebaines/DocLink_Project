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
import java.util.Map; // <--- New Import
import java.util.function.Function; // <--- New Import
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorTimeSlotRepository doctorTimeSlotRepository;

    public ResponseEntity<ViewSlotDto> viewSlot(Long slotId, LocalDate date) {
        Doctor_time_slots timeSlot = doctorTimeSlotRepository.findById(slotId).orElse(null);
        if (timeSlot == null) {
            return ResponseEntity.notFound().build();
        }

        // === CHANGE 1: Use a MAP instead of a Set ===
        // This maps the Seat Number -> The actual Appointment Object
        // So we can access the status later.
        Map<Integer, Appointment> bookedAppointments = appointmentRepository.findAllByTimeSlot_IdAndAppointmentDate(slotId, date)
                .stream()
                .collect(Collectors.toMap(Appointment::getSeatNumber, Function.identity()));

        // === CHANGE 2: Update the loop logic ===
        List<seatDto> allSeats = IntStream.rangeClosed(1, timeSlot.getTotalSeats())
                .mapToObj(seatNum -> {
                    seatDto seat = new seatDto();
                    seat.setSeatNumber(seatNum);

                    // If the map contains the seat number, get the REAL status from the appointment
                    if (bookedAppointments.containsKey(seatNum)) {
                        Appointment apt = bookedAppointments.get(seatNum);
                        seat.setStatus(apt.getStatus()); // <--- DYNAMIC STATUS (scheduled, completed, no_show)
                    } else {
                        seat.setStatus(null); // Available
                    }
                    return seat;
                })
                .collect(Collectors.toList());


        ViewSlotDto viewSlotDto = new ViewSlotDto();
        viewSlotDto.setDoctorName(timeSlot.getDoctor().getUser().getFirstName() + " " + timeSlot.getDoctor().getUser().getLastName());
        viewSlotDto.setHospitalName(timeSlot.getHospital().getHospitalName());
        viewSlotDto.setDate(date);

        // Force availability to TRUE as discussed
        viewSlotDto.setAvailability(true);

        // Update free seats calculation to use the map size
        viewSlotDto.setFreeSeats(timeSlot.getTotalSeats() - bookedAppointments.size());
        viewSlotDto.setTotalSeats(timeSlot.getTotalSeats());
        viewSlotDto.setTimePeriod(timeSlot.getStartTime().toString() + " - " + timeSlot.getEndTime().toString());
        viewSlotDto.setSeats(allSeats);

        return ResponseEntity.ok(viewSlotDto);
    }

    public ResponseEntity<List<LocalDate>> upcomingDates(Long slotId) {
        Doctor_time_slots slots = doctorTimeSlotRepository.findById(slotId)
                .orElse(null);

        if (slots == null) {
            return ResponseEntity.notFound().build();
        }

        DayOfWeek dayOfWeek = slots.getDayOfWeek();
        LocalDate today = LocalDate.now();

        List<LocalDate> upcomingDates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(4 * 7)
                .filter(date -> date.getDayOfWeek() == dayOfWeek)
                .collect(Collectors.toList());

        return ResponseEntity.ok(upcomingDates);
    }
}