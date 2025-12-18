package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernel.Entity.MedicalReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalReportRepository extends JpaRepository<MedicalReport, Long> {
}