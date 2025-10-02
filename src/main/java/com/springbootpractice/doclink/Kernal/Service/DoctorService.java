package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.DoctorAvailabilityRepository;
import com.springbootpractice.doclink.Dealer.DoctorRepository;
import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import com.springbootpractice.doclink.Kernal.Entity.Hospital;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_availability;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewDoctorDto;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewDoctorsDto;
import com.springbootpractice.doclink.Listner.Dto.Response.WorkPLaceDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<ViewDoctorDto> viewDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(Math.toIntExact(id)).orElse(null);
        if (doctor == null) {
            return ResponseEntity.notFound().build();
        }

        ViewDoctorDto dto = new ViewDoctorDto();
        dto.setImage(doctor.getImage());
        dto.setDoctorName(doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        dto.setYearOfExperience(doctor.getYearsExperience());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setQualification(doctor.getQualification());
        dto.setPhoneNumber(doctor.getUser().getPhoneNumber());
        dto.setEmail(doctor.getUser().getEmail());
        dto.setAddress(doctor.getUser().getAddress());

        List<Doctor_availability> availabilities =
                doctorAvailabilityRepository.findAllByDoctorIdWithHospital(id);

        List<WorkPLaceDto> workPlaces = availabilities.stream()
                .map(this::toWorkPlaceDto)
                .collect(Collectors.toList());

        dto.setWorkPlaces(workPlaces);

        return ResponseEntity.ok(dto);
    }

    private WorkPLaceDto toWorkPlaceDto(Doctor_availability da) {
        WorkPLaceDto w = new WorkPLaceDto();
        Hospital h = da.getHospital();

        w.setHospitalId(h.getHospitalId());
        w.setHospitalName(h.getHospitalName());
        w.setHospitalAddress(h.getAddress());
        w.setPhoneNumber(h.getPhoneNumber());

        w.setAvailableSeats(null);
        w.setTotalSeats(da.getTotalSeats());

        w.setTimePeriod(formatTimePeriod(da.getStartTime(), da.getEndTime()));
        w.setAvailability(Boolean.TRUE.equals(da.getAvailability()) ? "AVAILABLE" : "UNAVAILABLE");
        return w;
    }

    private String formatTimePeriod(LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        String s = start != null ? start.format(fmt) : "";
        String e = end != null ? end.format(fmt) : "";
        if (s.isEmpty() && e.isEmpty()) return "";
        if (s.isEmpty()) return "- " + e;
        if (e.isEmpty()) return s + " -";
        return s + " - " + e;
    }

    public ResponseEntity<List<ViewDoctorsDto>> viewDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();

        List<ViewDoctorsDto> doctorsAvailable = doctors.stream().map(this::toViewDoctorsDto).collect(Collectors.toList());
        return ResponseEntity.ok(doctorsAvailable);
    }

    private ViewDoctorsDto toViewDoctorsDto(Doctor doctor) {
        ViewDoctorsDto dto = new ViewDoctorsDto();

        dto.setImage(String.valueOf(doctor.getImage()));
        dto.setDoctorId(doctor.getDoctorId());
        dto.setName(doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName());
        dto.setSpecialization(String.valueOf(doctor.getSpecialization()));
        return dto;
    }
}