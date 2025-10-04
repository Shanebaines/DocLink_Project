package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernal.Relations.Doctor_availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<Doctor_availability, Long> {

    List<Doctor_availability> findByDoctorDoctorIdAndHospitalHospitalIdOrderByDayOfWeekAscStartTimeAsc(
            Long doctorId, Long hospitalId
    );

    List<Doctor_availability> findByDoctorDoctorIdAndHospitalHospitalIdAndAvailabilityTrueOrderByDayOfWeekAscStartTimeAsc(
            Long doctorId, Long hospitalId
    );
}