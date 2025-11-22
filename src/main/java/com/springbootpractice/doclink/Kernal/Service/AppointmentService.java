package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.AppointmentRepository;
import com.springbootpractice.doclink.Dealer.PatientRepository;
import com.springbootpractice.doclink.Dealer.DoctorTimeSlotRepository;
import com.springbootpractice.doclink.Kernal.Entity.Appointment;
import com.springbootpractice.doclink.Kernal.Entity.Patient;
import com.springbootpractice.doclink.Kernal.Enums.AppointmentStatusType;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_time_slots;
import com.springbootpractice.doclink.Listner.Dto.Request.CreateAppointmentRequestDto;
import com.springbootpractice.doclink.Listner.Dto.Request.patientSeatDto;
import com.springbootpractice.doclink.Listner.Dto.Request.UpdateStatusRequestDto;
import com.springbootpractice.doclink.Listner.Dto.Response.seatDto;
import com.springbootpractice.doclink.Listner.Dto.Response.seatViewDto;
import com.springbootpractice.doclink.Listner.Dto.Response.viewAppointmentsDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        List<Appointment> existingAppointments = appointmentRepository.findAllByTimeSlot_IdAndAppointmentDate(
                requestDto.getTimeSlotId(), requestDto.getAppointmentDate());

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
        newAppointment.setCreatedAt(LocalDateTime.now());

        return appointmentRepository.save(newAppointment);
    }

    /**
     * Updates the status of an existing appointment (Admin use).
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

        LocalDateTime slotEndTime = LocalDateTime.of(
                appointment.getAppointmentDate(), appointment.getTimeSlot().getEndTime());

        if (newStatus == AppointmentStatusType.no_show && slotEndTime.isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot mark an appointment as a no-show before its scheduled time has passed.");
        }

        appointment.setStatus(newStatus);
        appointment.setNotes(requestDto.getNotes());
        appointment.setUpdatedAt(LocalDateTime.now());

        return appointmentRepository.save(appointment);
    }

    /**
     * Retrieves all appointments for a specific patient.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<List<viewAppointmentsDto>> viewAppointments(Long id) {
        List<Appointment> optionalAppointment =
                appointmentRepository.findByPatientPatientIdOrderByAppointmentDateDesc(id);

        List<viewAppointmentsDto> appointments = optionalAppointment.stream()
                .map(this::toViewAppointmentsDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(appointments);
    }

    /**
     * Converts an Appointment entity to DTO for view purposes.
     */
    private viewAppointmentsDto toViewAppointmentsDto(Appointment appointment) {
        viewAppointmentsDto dto = new viewAppointmentsDto();

        dto.setHospitalId(appointment.getTimeSlot().getHospital().getHospitalId());
        dto.setHospitalName(appointment.getTimeSlot().getHospital().getHospitalName());
        dto.setDoctorId(appointment.getTimeSlot().getDoctor().getDoctorId());

        String doctorName = appointment.getTimeSlot().getDoctor().getUser().getFirstName() + " " +
                appointment.getTimeSlot().getDoctor().getUser().getLastName();
        dto.setDoctorName(doctorName);

        dto.setSeatNumber(appointment.getSeatNumber());
        dto.setTimeSlot(appointment.getTimeSlot().getStartTime() + " - " + appointment.getTimeSlot().getEndTime());
        dto.setAvailableTime(appointment.getAppointmentDate().atStartOfDay());
        dto.setDayOfWeek(appointment.getTimeSlot().getDayOfWeek());
        dto.setAppointmentStatus(appointment.getStatus());
        return dto;
    }

    /**
     * Used by doctors to mark an appointment as completed.
     */
    @Transactional
    public Appointment updateAppointmentStatusByDoctor(@Valid patientSeatDto requestDto,
                                                       AppointmentStatusType appointmentStatus) {
        // Validate that only completed or not_completed can be set by doctor
        if (appointmentStatus != AppointmentStatusType.completed &&
                appointmentStatus != AppointmentStatusType.not_completed) {
            throw new IllegalArgumentException(
                    "Doctor can only mark appointments as 'completed' or 'not_completed'. Received: " + appointmentStatus);
        }

        Long slotId = requestDto.getSlot_id();
        LocalDate date = requestDto.getAppointment_date();
        Integer seat = requestDto.getSeat_number();

        Appointment appointment = appointmentRepository
                .findByTimeSlot_IdAndAppointmentDateAndSeatNumber(slotId, date, seat)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("No appointment found for slot=%d, date=%s, seat=%d",
                                slotId, date, seat)));

        // Validate current status
        if (appointment.getStatus() == AppointmentStatusType.cancelled) {
            throw new IllegalStateException("Cannot update a cancelled appointment.");
        }

        if (appointment.getStatus() == appointmentStatus) {
            throw new IllegalStateException(
                    String.format("Appointment is already marked as %s.", appointmentStatus.name()));
        }

        appointment.setStatus(appointmentStatus);
        appointment.setUpdatedAt(LocalDateTime.now());

        return appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<seatViewDto> viewSeat(patientSeatDto request) {
        Appointment appointment = appointmentRepository
                .findByTimeSlot_IdAndAppointmentDateAndSeatNumber(
                        request.getSlot_id(),
                        request.getAppointment_date(),
                        request.getSeat_number())
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("No appointment found for slot=%d, date=%s, seat=%d",
                                request.getSlot_id(), request.getAppointment_date(), request.getSeat_number())));

        AppointmentStatusType status = appointment.getStatus();
        if (!(status == AppointmentStatusType.completed || status == AppointmentStatusType.scheduled)) {
            return ResponseEntity.notFound().build();
        }

        seatDto seat = new seatDto();
        seat.setSeatNumber(request.getSeat_number());
        seat.setStatus(status);

        seatViewDto response = new seatViewDto();
        response.setPatientId(appointment.getPatient().getPatientId());
        response.setPatientName(appointment.getPatient().getUser().getFirstName() + " " +
                appointment.getPatient().getUser().getLastName());
        response.setSeat(seat);

        return ResponseEntity.ok(response);
    }
}