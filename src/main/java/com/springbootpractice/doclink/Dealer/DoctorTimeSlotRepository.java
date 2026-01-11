package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernel.Relations.Doctor_time_slots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorTimeSlotRepository extends JpaRepository<Doctor_time_slots, Long> {
    List<Doctor_time_slots> findByDoctorDoctorIdAndHospitalHospitalIdOrderByDayOfWeekAscStartTimeAsc(
            Long doctorId, Long hospitalId);
}