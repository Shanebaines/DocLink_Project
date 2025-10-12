package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.AppointmentRepository;
import com.springbootpractice.doclink.Dealer.DoctorAvailabilityRepository;
import com.springbootpractice.doclink.Dealer.DoctorTimeSlotRepository;
import com.springbootpractice.doclink.Kernal.Entity.Appointment;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_time_slots;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewSlotDto;
import com.springbootpractice.doclink.Listner.Dto.Response.seatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
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
        List<Appointment> appointmentList = appointmentRepository.findAllByTimeSlot_IdAndAppointmentDate(slotId, date);
        Integer numberOfSeats = appointmentList.size();
        Integer totalSeats = timeSlot.getTotalSeats();

        List<seatsDto> seats = appointmentList.stream()
                .map(this::toSeatsDto)
                .collect(Collectors.toList());

        ViewSlotDto viewSlotDto = new ViewSlotDto();
        viewSlotDto.setDoctorName(timeSlot.getDoctor().getUser().getFirstName()+" "+timeSlot.getDoctor().getUser().getLastName());
        viewSlotDto.setHospitalName(timeSlot.getHospital().getHospitalName());
        viewSlotDto.setDate(date);
        viewSlotDto.setAvailability(doctorAvailabilityRepository.getAvailabilityBySlotId(slotId));
        viewSlotDto.setFreeSeats(totalSeats - numberOfSeats);
        viewSlotDto.setTotalSeats(totalSeats);
        viewSlotDto.setTimePeriod(timeSlot.getStartTime()+" - "+timeSlot.getEndTime());
        viewSlotDto.setSeats(seats);
        return ResponseEntity.ok(viewSlotDto);
    }

    private seatsDto toSeatsDto(Appointment appointment) {
        seatsDto seatsDto = new seatsDto();
        seatsDto.setSeatNumber(appointment.getSeatNumber());
        seatsDto.setStatus(appointment.getStatus());
        return seatsDto;
    }

    //if can, use calendar API for find holy days and others

    public ResponseEntity<List<LocalDate>> upcomingDates(Long slotId) {
        Doctor_time_slots slots = doctorTimeSlotRepository.findById(slotId)
                .orElse(null);

        if (slots == null) {
            return ResponseEntity.notFound().build();
        }

        DayOfWeek dayOfWeek = slots.getDayOfWeek();
        LocalDate today = LocalDate.now();

        List<LocalDate> upcomingDates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(4 * 7) // 4 weeks = 28 days
                .filter(date -> date.getDayOfWeek() == dayOfWeek)
                .collect(Collectors.toList());

        return ResponseEntity.ok(upcomingDates);
    }
}
