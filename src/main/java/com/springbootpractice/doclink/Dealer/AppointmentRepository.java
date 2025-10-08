package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernal.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // No custom queries needed right now
}