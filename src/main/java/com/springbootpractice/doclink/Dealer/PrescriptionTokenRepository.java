package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernal.Relations.PrescriptionToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionTokenRepository extends JpaRepository<PrescriptionToken, Long> {

    Optional<PrescriptionToken> findByTokenAndActiveTrue(String token);

    boolean existsByPrescriptionPrescriptionId(Long prescriptionId);

    List<PrescriptionToken> findByPrescription_MedicalRecord_Patient_PatientId(Long patientId);
}