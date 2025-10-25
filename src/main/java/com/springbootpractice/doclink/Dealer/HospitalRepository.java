package com.springbootpractice.doclink.Dealer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springbootpractice.doclink.Kernal.Entity.Hospital;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
}
