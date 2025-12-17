package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernel.Entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    @Query("SELECT p FROM Prescription p " +
            "LEFT JOIN FETCH p.prescriptionMedications pm " +
            "LEFT JOIN FETCH pm.medication " +
            "WHERE p.prescriptionId = :id")
    Optional<Prescription> findByIdWithMedications(@Param("id") Long id);
}