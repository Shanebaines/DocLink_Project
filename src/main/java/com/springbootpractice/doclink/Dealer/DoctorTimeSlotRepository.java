package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernal.Relations.Doctor_time_slots;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DoctorTimeSlotRepository extends JpaRepository<Doctor_time_slots, Long> {
    List<Doctor_time_slots> findByDoctorDoctorIdAndHospitalHospitalIdOrderByDayOfWeekAscStartTimeAsc(
            Long doctorId, Long hospitalId);
}