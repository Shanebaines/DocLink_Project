package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.DoctorRepository;
import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewDoctorDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class DoctorService {
    private final DoctorRepository doctorRepository;
    public  ResponseEntity<ViewDoctorDto> viewDoctor(Integer id) {
        ViewDoctorDto viewDoctorDto = new ViewDoctorDto();
        Optional<Doctor> optionalDoctor = doctorRepository.findById(id);
        if (optionalDoctor.isPresent()) {
            Doctor doctor = optionalDoctor.get();
            viewDoctorDto.setImage(doctor.getImage());
            viewDoctorDto.setDoctorName(doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName());
            viewDoctorDto.setLicenseNumber(String.valueOf(doctor.getLicenseNumber()));
            viewDoctorDto.setYearOfExperience(doctor.getYearsExperience());
            viewDoctorDto.setSpecialization(String.valueOf(doctor.getSpecialization()));
            viewDoctorDto.setQualification(String.valueOf(doctor.getQualification()));
            viewDoctorDto.setPhoneNumber(doctor.getUser().getPhoneNumber());
            viewDoctorDto.setEmail(doctor.getUser().getEmail());
            viewDoctorDto.setAddress(doctor.getUser().getAddress());
            return ResponseEntity.ok().body(viewDoctorDto);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

}
