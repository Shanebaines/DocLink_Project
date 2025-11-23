package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernal.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Method for the "My Appointments" page.
    List<Appointment> findByPatientPatientIdOrderByAppointmentDateDesc(Long patientId);

    // Method for the ScheduleService to find booked seats.
    List<Appointment> findAllByTimeSlot_IdAndAppointmentDate(Long slotId, LocalDate date);

    Optional<Appointment> findByTimeSlot_IdAndAppointmentDateAndSeatNumber(Long slotId, LocalDate date, Integer seat);

    // Fetch all appointments for a specific time slot ID on a specific date
    List<Appointment> findByTimeSlotIdAndAppointmentDate(Long timeSlotId, LocalDate appointmentDate);

}