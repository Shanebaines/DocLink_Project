package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernal.Entity.Appointment;
import com.springbootpractice.doclink.Kernal.Enums.AppointmentStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Same-day window: [startTime, endTime)
    @Query("""
           select count(a) from Appointment a
           where a.doctor.doctorId = :doctorId
             and a.hospital.hospitalId = :hospitalId
             and a.appointmentDate = :date
             and a.appointmentTime >= :startTime
             and a.appointmentTime < :endTime
             and a.status in :statuses
           """)
    int countOnDateBetweenTimes(@Param("doctorId") Long doctorId,
                                @Param("hospitalId") Long hospitalId,
                                @Param("date") LocalDate date,
                                @Param("startTime") LocalTime startTime,
                                @Param("endTime") LocalTime endTime,
                                @Param("statuses") List<AppointmentStatusType> statuses);

    // Partial-day: from a given time to the end of that date
    @Query("""
           select count(a) from Appointment a
           where a.doctor.doctorId = :doctorId
             and a.hospital.hospitalId = :hospitalId
             and a.appointmentDate = :date
             and a.appointmentTime >= :startTime
             and a.status in :statuses
           """)
    int countOnDateFromTime(@Param("doctorId") Long doctorId,
                            @Param("hospitalId") Long hospitalId,
                            @Param("date") LocalDate date,
                            @Param("startTime") LocalTime startTime,
                            @Param("statuses") List<AppointmentStatusType> statuses);

    // Partial-day: from midnight to a given time (exclusive)
    @Query("""
           select count(a) from Appointment a
           where a.doctor.doctorId = :doctorId
             and a.hospital.hospitalId = :hospitalId
             and a.appointmentDate = :date
             and a.appointmentTime < :endTime
             and a.status in :statuses
           """)
    int countOnDateBeforeTime(@Param("doctorId") Long doctorId,
                              @Param("hospitalId") Long hospitalId,
                              @Param("date") LocalDate date,
                              @Param("endTime") LocalTime endTime,
                              @Param("statuses") List<AppointmentStatusType> statuses);
}