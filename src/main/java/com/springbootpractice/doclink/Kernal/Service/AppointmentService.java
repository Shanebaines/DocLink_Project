package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.AppointmentRepository;
import com.springbootpractice.doclink.Dealer.DoctorTimeSlotRepository;
import com.springbootpractice.doclink.Kernal.Entity.Appointment;
import com.springbootpractice.doclink.Listner.Dto.Response.viewAppointmentsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AppointmentService {
    public final AppointmentRepository appointmentRepository;
    public final DoctorTimeSlotRepository doctorTimeSlotsRepository;

    public ResponseEntity<List<viewAppointmentsDto>> viewAppointments(Long id) {
        List<Appointment> optionalAppointment = appointmentRepository.findAllByPatient_PatientId(id);
        List<viewAppointmentsDto> appointments = optionalAppointment.stream()
                .map(this::toViewAppointmentsDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(appointments);
    }
    private viewAppointmentsDto toViewAppointmentsDto(Appointment appointment) {
        viewAppointmentsDto viewAppointmentsDto = new viewAppointmentsDto();
        Long HospitalId = appointment.getTimeSlot().getHospital().getHospitalId();
        String HospitalName = appointment.getTimeSlot().getHospital().getHospitalName();
        Long  DoctorId = appointment.getTimeSlot().getDoctor().getDoctorId();
        String DoctorName = appointment.getTimeSlot().getDoctor().getUser().getFirstName()
                + " " + appointment.getTimeSlot().getDoctor().getUser().getLastName();

        viewAppointmentsDto.setHospitalId(HospitalId);
        viewAppointmentsDto.setHospitalName(HospitalName);
        viewAppointmentsDto.setDoctorId(DoctorId);
        viewAppointmentsDto.setDoctorName(DoctorName);
        viewAppointmentsDto.setSeatNumber(appointment.getSeatNumber());
        viewAppointmentsDto.setTimeSlot(appointment.getTimeSlot().getStartTime().toString()+"-"+appointment.getTimeSlot().getEndTime().toString());
        viewAppointmentsDto.setAvailableTime(null);
        viewAppointmentsDto.setDayOfWeek(appointment.getTimeSlot().getDayOfWeek());

        return viewAppointmentsDto;
    }
}
