package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernal.Relations.Doctors_in_Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorsInHospitalRepository extends JpaRepository<Doctors_in_Hospital, Integer> {

    @Query("""
           select dih 
           from Doctors_in_Hospital dih 
           join fetch dih.hospital h 
           where dih.doctor.doctorId = :doctorId
           """)
    List<Doctors_in_Hospital> findAllByDoctorIdWithHospital(@Param("doctorId") Long doctorId);
}