package com.springbootpractice.doclink.Dealer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springbootpractice.doclink.Kernal.Relations.Doctor_time_slots;

@Repository
public interface DoctorTimeSlotRepository extends JpaRepository<Doctor_time_slots, Long> {
    List<Doctor_time_slots> findByDoctorDoctorIdAndHospitalHospitalIdOrderByDayOfWeekAscStartTimeAsc(
            Long doctorId, Long hospitalId);
        List<Doctor_time_slots> findByDoctorDoctorIdAndHospitalHospitalNameOrderByDayOfWeekAscStartTimeAsc(
            Long doctorId, String hospitalName);
}