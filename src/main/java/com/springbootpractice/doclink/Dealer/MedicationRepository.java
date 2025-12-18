package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernel.Entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
}