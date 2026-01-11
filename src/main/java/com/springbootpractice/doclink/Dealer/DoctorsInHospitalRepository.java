package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernel.Relations.Doctors_in_Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import com.springbootpractice.doclink.Kernel.Entity.Doctor;
import org.springframework.data.repository.query.Param;

public interface DoctorsInHospitalRepository extends JpaRepository<Doctors_in_Hospital, Long> {

    @Query("""
           select dih
           from Doctors_in_Hospital dih
           join fetch dih.hospital h
           where dih.doctor.doctorId = :doctorId
           """)
    List<Doctors_in_Hospital> findAllByDoctorIdWithHospital(Long doctorId);

    @Query("SELECT dih.doctor FROM Doctors_in_Hospital dih WHERE dih.hospital.hospitalId = :hospitalId")
    List<Doctor> findDoctorsByHospitalId(@Param("hospitalId") Long hospitalId);
}