package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernal.Relations.Doctor_availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<Doctor_availability, Long> {

    // Orders by slot fields (dayOfWeek, startTime) and fetches the slot to avoid N+1
    @Query("""
           select da
           from Doctor_availability da
           join fetch da.slot s
           where da.doctor.doctorId = :doctorId
             and da.hospital.hospitalId = :hospitalId
           order by s.dayOfWeek asc, s.startTime asc
           """)
    List<Doctor_availability> findByDoctorDoctorIdAndHospitalHospitalIdOrderByDayOfWeekAscStartTimeAsc(
            @Param("doctorId") Long doctorId,
            @Param("hospitalId") Long hospitalId
    );

    // Same as above but filters availability = true
    @Query("""
           select da
           from Doctor_availability da
           join fetch da.slot s
           where da.doctor.doctorId = :doctorId
             and da.hospital.hospitalId = :hospitalId
             and da.availability = true
           order by s.dayOfWeek asc, s.startTime asc
           """)
    List<Doctor_availability> findByDoctorDoctorIdAndHospitalHospitalIdAndAvailabilityTrueOrderByDayOfWeekAscStartTimeAsc(
            @Param("doctorId") Long doctorId,
            @Param("hospitalId") Long hospitalId
    );
}