package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernel.Entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
}

