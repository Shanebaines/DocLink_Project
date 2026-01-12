package com.springbootpractice.doclink.Dealer;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springbootpractice.doclink.Kernal.Relations.PatientDoctorBookmark;

@Repository
public interface PatientDoctorBookmarkRepository extends JpaRepository<PatientDoctorBookmark, Long> {
    boolean existsByPatientPatientIdAndDoctorDoctorId(Long patientId, Long doctorId);
    Optional<PatientDoctorBookmark> findByPatientPatientIdAndDoctorDoctorId(Long patientId, Long doctorId);
    List<PatientDoctorBookmark> findByPatientPatientIdOrderByCreatedAtDesc(Long patientId);
}
