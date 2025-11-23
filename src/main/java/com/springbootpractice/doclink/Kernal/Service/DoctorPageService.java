package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.AppointmentRepository;
import com.springbootpractice.doclink.Dealer.DoctorTimeSlotRepository;
import com.springbootpractice.doclink.Kernal.Entity.Appointment;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_time_slots;
import com.springbootpractice.doclink.Listner.Dto.Response.DoctorAppointmentDetailDto;
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

    /**
     * 1. GRID VIEW: Returns the layout of seats and their status.
     * Does NOT return sensitive patient info here.
     */
    public DoctorSlotOverviewDto getSlotDetailsWithSeats(Long slotId, LocalDate date) {
        Doctor_time_slots timeSlot = doctorTimeSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Time Slot not found"));

        List<Appointment> appointments = appointmentRepository.findByTimeSlotIdAndAppointmentDate(slotId, date);

        Map<Integer, Appointment> appointmentMap = appointments.stream()
                .collect(Collectors.toMap(Appointment::getSeatNumber, Function.identity()));

        List<SeatStatusDto> seatGrid = new ArrayList<>();
        for (int i = 1; i <= timeSlot.getTotalSeats(); i++) {
            if (appointmentMap.containsKey(i)) {
                // Seat is TAKEN
                Appointment apt = appointmentMap.get(i);
                seatGrid.add(SeatStatusDto.builder()
                        .seatNumber(i)
                        .status(apt.getStatus().name()) // "scheduled", "completed", etc.
                        // Note: We removed appointmentId and patientName from here
                        .build());
            } else {
                // Seat is EMPTY
                seatGrid.add(SeatStatusDto.builder()
                        .seatNumber(i)
                        .status("AVAILABLE")
                        .build());
            }
        }

        return DoctorSlotOverviewDto.builder()
                .slotId(timeSlot.getId())
                .hospitalName(timeSlot.getHospital().getHospitalName())
                // Removed doctorName and date as requested
                .timeRange(timeSlot.getStartTime() + " - " + timeSlot.getEndTime())
                .totalSeats(timeSlot.getTotalSeats())
                .bookedSeats(appointments.size())
                .isSlotActive(timeSlot.getAvailability())
                .seats(seatGrid)
                .build();
    }

    /**
     * 2. DETAIL VIEW: Returns specific patient details when a seat is clicked.
     */
    public DoctorAppointmentDetailDto getAppointmentDetailsBySeatCoordinates(Long slotId, LocalDate date, Integer seatNumber) {

        Appointment apt = appointmentRepository.findByTimeSlotIdAndAppointmentDateAndSeatNumber(slotId, date, seatNumber)
                .orElseThrow(() -> new RuntimeException("No appointment found for Slot " + slotId + " on " + date + " at Seat " + seatNumber));

        return DoctorAppointmentDetailDto.builder()
                .appointmentId(apt.getAppointmentId())
                .patientName(apt.getPatient().getUser().getFirstName() + " " + apt.getPatient().getUser().getLastName())
                .patientContact(apt.getPatient().getUser().getPhoneNumber())
                .insuranceNumber(apt.getPatient().getInsuranceNumber())
                .gender(apt.getPatient().getUser().getGender().name())
                .reasonForVisit(apt.getReason())
                .status(apt.getStatus().name())
                .build();
    }
}