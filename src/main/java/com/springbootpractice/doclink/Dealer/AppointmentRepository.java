package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernal.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Method for the "My Appointments" page
    List<Appointment> findByPatientPatientIdOrderByAppointmentDateDesc(Long patientId);

    // Method for the ScheduleService to find booked seats
    List<Appointment> findAllByTimeSlot_IdAndAppointmentDate(Long slotId, LocalDate date);
}