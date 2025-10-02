package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernal.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Integer> {
    Integer countByHospitalHospitalId(Long hospitalId);
}
