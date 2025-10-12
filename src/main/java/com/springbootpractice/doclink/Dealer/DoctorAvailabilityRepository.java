package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernal.Relations.Doctor_availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DoctorAvailabilityRepository extends JpaRepository<Doctor_availability, Long> {

    @Query("""
       select da.availability
       from Doctor_availability da
       where da.slot.id = :slotId
       """)
    Boolean getAvailabilityBySlotId(@Param("slotId") Long slotId);
}