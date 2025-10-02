package com.springbootpractice.doclink.Dealer;


import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewDoctorsDto;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Integer> {
    List<Doctor> findAll();
}
