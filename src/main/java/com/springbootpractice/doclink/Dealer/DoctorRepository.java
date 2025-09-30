package com.springbootpractice.doclink.Dealer;


import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Integer> {
}
