package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernel.Relations.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByPatientPatientIdOrderByCreatedAtDesc(Long patientId);

    List<MedicalRecord> findByDoctorDoctorIdOrderByCreatedAtDesc(Long doctorId);

    @Query("SELECT mr FROM MedicalRecord mr " +
            "LEFT JOIN FETCH mr.medicalReport " +
            "LEFT JOIN FETCH mr.prescription p " +
            "LEFT JOIN FETCH p.prescriptionMedications " +
            "WHERE mr.medicalRecordId = :id")
    Optional<MedicalRecord> findByIdWithDetails(@Param("id") Long id);
}