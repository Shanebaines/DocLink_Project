package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernal.Relations.Doctor_availability;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_availability.DoctorAvailabilityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<Doctor_availability, DoctorAvailabilityId> {

    // Use this if you want hospital fields eagerly available (no LazyInitializationException)
    @Query("select da from Doctor_availability da join fetch da.hospital h where da.doctor.doctorId = :doctorId")
    List<Doctor_availability> findAllByDoctorIdWithHospital(@Param("doctorId") Long doctorId);
}