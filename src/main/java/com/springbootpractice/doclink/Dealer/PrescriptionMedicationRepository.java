package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernal.Entity.PrescriptionMedication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionMedicationRepository extends JpaRepository<PrescriptionMedication, Long> {
}