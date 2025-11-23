package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.AppointmentRepository;
import com.springbootpractice.doclink.Dealer.DoctorTimeSlotRepository;
import com.springbootpractice.doclink.Kernal.Entity.Appointment;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_time_slots;
import com.springbootpractice.doclink.Listner.Dto.Response.DoctorSlotOverviewDto;
import com.springbootpractice.doclink.Listner.Dto.Response.SeatStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorPageService {

    private final DoctorTimeSlotRepository doctorTimeSlotRepository;
    private final AppointmentRepository appointmentRepository;

    public DoctorSlotOverviewDto getSlotDetailsWithSeats(Long slotId, LocalDate date) {
        // 1. Get the slot details (total seats, time, etc.)
        Doctor_time_slots timeSlot = doctorTimeSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Time Slot not found"));

        // 2. Get actual bookings
        List<Appointment> appointments = appointmentRepository.findByTimeSlotIdAndAppointmentDate(slotId, date);

        // 3. Map appointments by seat number for easy lookup
        Map<Integer, Appointment> appointmentMap = appointments.stream()
                .collect(Collectors.toMap(Appointment::getSeatNumber, Function.identity()));

        // 4. Generate the grid (1 to Total Seats)
        List<SeatStatusDto> seatGrid = new ArrayList<>();
        for (int i = 1; i <= timeSlot.getTotalSeats(); i++) {
            if (appointmentMap.containsKey(i)) {
                // Seat is BOOKED - Show Patient Info
                Appointment apt = appointmentMap.get(i);
                seatGrid.add(SeatStatusDto.builder()
                        .seatNumber(i)
                        .status("BOOKED")
                        .appointmentId(apt.getAppointmentId())
                        .patientName(apt.getPatient().getUser().getFirstName() + " " + apt.getPatient().getUser().getLastName())
                        .patientContact(apt.getPatient().getUser().getPhoneNumber())
                        .build());
            } else {
                // Seat is AVAILABLE
                seatGrid.add(SeatStatusDto.builder()
                        .seatNumber(i)
                        .status("AVAILABLE")
                        .appointmentId(null)
                        .patientName(null)
                        .patientContact(null)
                        .build());
            }
        }

        // 5. Return the full DTO
        return DoctorSlotOverviewDto.builder()
                .slotId(timeSlot.getId())
                .hospitalName(timeSlot.getHospital().getHospitalName())
                .doctorName(timeSlot.getDoctor().getUser().getFirstName() + " " + timeSlot.getDoctor().getUser().getLastName())
                .date(date)
                .timeRange(timeSlot.getStartTime() + " - " + timeSlot.getEndTime())
                .totalSeats(timeSlot.getTotalSeats())
                .bookedSeats(appointments.size())
                .isSlotActive(timeSlot.getAvailability())
                .seats(seatGrid)
                .build();
    }
}