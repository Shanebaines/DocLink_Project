package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.AppointmentRepository;
import com.springbootpractice.doclink.Dealer.PatientRepository;
import com.springbootpractice.doclink.Dealer.DoctorTimeSlotRepository;
import com.springbootpractice.doclink.Kernal.Entity.Appointment;
import com.springbootpractice.doclink.Kernal.Entity.Patient;
import com.springbootpractice.doclink.Kernal.Enums.AppointmentStatusType;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_time_slots;
import com.springbootpractice.doclink.Listner.Dto.Request.CreateAppointmentRequestDto;
import com.springbootpractice.doclink.Listner.Dto.Request.UpdateStatusRequestDto;
import com.springbootpractice.doclink.Listner.Dto.Response.viewAppointmentsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorTimeSlotRepository doctorTimeSlotRepository;

    /**
     * Creates a new appointment for a specific seat in a time slot.
     */
    @Transactional
    public Appointment createAppointment(CreateAppointmentRequestDto requestDto) {
        Patient patient = patientRepository.findById(requestDto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + requestDto.getPatientId()));

        Doctor_time_slots timeSlot = doctorTimeSlotRepository.findById(requestDto.getTimeSlotId())
                .orElseThrow(() -> new IllegalArgumentException("Time Slot not found with ID: " + requestDto.getTimeSlotId()));

        // Check for double booking of the same seat on the same day for the same slot
        List<Appointment> existingAppointments = appointmentRepository.findAllByTimeSlot_IdAndAppointmentDate(requestDto.getTimeSlotId(), requestDto.getAppointmentDate());
        boolean isSeatTaken = existingAppointments.stream()
                .anyMatch(a -> a.getSeatNumber().equals(requestDto.getSeatNumber()));

        if (isSeatTaken) {
            throw new IllegalStateException("Seat number " + requestDto.getSeatNumber() + " is already taken for this date.");
        }

        Appointment newAppointment = new Appointment();
        newAppointment.setPatient(patient);
        newAppointment.setTimeSlot(timeSlot);
        newAppointment.setDoctor(timeSlot.getDoctor());
        newAppointment.setHospital(timeSlot.getHospital());
        newAppointment.setAppointmentDate(requestDto.getAppointmentDate());
        newAppointment.setSeatNumber(requestDto.getSeatNumber());
        newAppointment.setReason(requestDto.getReason());
        newAppointment.setStatus(AppointmentStatusType.scheduled);

        return appointmentRepository.save(newAppointment);
    }

    /**
     * Updates the status of an existing appointment.
     */
    @Transactional
    public Appointment updateAppointmentStatus(Long appointmentId, UpdateStatusRequestDto requestDto) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        AppointmentStatusType currentStatus = appointment.getStatus();
        AppointmentStatusType newStatus = requestDto.getNewStatus();

        if (currentStatus == AppointmentStatusType.completed || currentStatus == AppointmentStatusType.cancelled) {
            throw new IllegalStateException("Cannot update a " + currentStatus + " appointment.");
        }

        LocalDateTime slotEndTime = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getTimeSlot().getEndTime());
        if (newStatus == AppointmentStatusType.no_show && slotEndTime.isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot mark an appointment as a no-show before its scheduled time has passed.");
        }

        appointment.setStatus(newStatus);
        appointment.setNotes(requestDto.getNotes());

        return appointmentRepository.save(appointment);
    }

    /**
     * Retrieves all appointments for a specific patient.
     * This is your existing code.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<List<viewAppointmentsDto>> viewAppointments(Long id) {
        List<Appointment> optionalAppointment = appointmentRepository.findByPatientPatientIdOrderByAppointmentDateDesc(id);
        List<viewAppointmentsDto> appointments = optionalAppointment.stream()
                .map(this::toViewAppointmentsDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(appointments);
    }

    /**
     * Helper method to convert an Appointment entity to your specific DTO.
     * This is also your existing code.
     */
    private viewAppointmentsDto toViewAppointmentsDto(Appointment appointment) {
        viewAppointmentsDto viewAppointmentsDto = new viewAppointmentsDto();
        Long HospitalId = appointment.getTimeSlot().getHospital().getHospitalId();
        String HospitalName = appointment.getTimeSlot().getHospital().getHospitalName();
        Long DoctorId = appointment.getTimeSlot().getDoctor().getDoctorId();
        String DoctorName = appointment.getTimeSlot().getDoctor().getUser().getFirstName()
                + " " + appointment.getTimeSlot().getDoctor().getUser().getLastName();

        viewAppointmentsDto.setHospitalId(HospitalId);
        viewAppointmentsDto.setHospitalName(HospitalName);
        viewAppointmentsDto.setDoctorId(DoctorId);
        viewAppointmentsDto.setDoctorName(DoctorName);
        viewAppointmentsDto.setSeatNumber(appointment.getSeatNumber());
        viewAppointmentsDto.setTimeSlot(appointment.getTimeSlot().getStartTime().toString() + "-" + appointment.getTimeSlot().getEndTime().toString());
        viewAppointmentsDto.setAvailableTime(appointment.getAppointmentDate().atStartOfDay()); // Using atStartOfDay for LocalDateTime
        viewAppointmentsDto.setDayOfWeek(appointment.getTimeSlot().getDayOfWeek());

        return viewAppointmentsDto;
    }
}