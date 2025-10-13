package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.DoctorAvailabilityRepository;
import com.springbootpractice.doclink.Dealer.DoctorRepository;
import com.springbootpractice.doclink.Dealer.DoctorsInHospitalRepository;
import com.springbootpractice.doclink.Dealer.DoctorTimeSlotRepository;
import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import com.springbootpractice.doclink.Kernal.Entity.Hospital;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_time_slots;
import com.springbootpractice.doclink.Kernal.Relations.Doctors_in_Hospital;
import com.springbootpractice.doclink.Listner.Dto.Response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorsInHospitalRepository doctorsInHospitalRepository;
    private final DoctorTimeSlotRepository doctorTimeSlotRepository;

    public ResponseEntity<ViewDoctorDto> viewDoctor(Long id) {
        Optional<Doctor> optDoctor = doctorRepository.findById(id);
        if (optDoctor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Doctor doctor = optDoctor.get();

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

        List<Doctors_in_Hospital> places = doctorsInHospitalRepository.findAllByDoctorIdWithHospital(id);

        List<WorkPLaceDto> workPlaces = places.stream()
                .map(dih -> toWorkPlaceDto(dih, id))
                .collect(Collectors.toList());

        dto.setWorkPlaces(workPlaces);
        return ResponseEntity.ok(dto);
    }

    private WorkPLaceDto toWorkPlaceDto(Doctors_in_Hospital dih, Long doctorId) {
        Hospital hospital = dih.getHospital();

        WorkPLaceDto workplaceDto = new WorkPLaceDto();
        workplaceDto.setHospitalId(hospital.getHospitalId());
        workplaceDto.setHospitalName(hospital.getHospitalName());
        workplaceDto.setGpsLocation(hospital.getGpsLocation());
        workplaceDto.setHospitalAddress(hospital.getAddress());
        workplaceDto.setPhoneNumber(hospital.getPhoneNumber());

        List<Doctor_time_slots> timeSlots =
                doctorTimeSlotRepository.findByDoctorDoctorIdAndHospitalHospitalIdOrderByDayOfWeekAscStartTimeAsc(
                        doctorId, hospital.getHospitalId());

        List<AvailableSlotsDto> slotDtos = timeSlots.stream()
                .map(this::toAvailableSlotDto)
                .collect(Collectors.toList());

        workplaceDto.setAvailableSlots(slotDtos);
        return workplaceDto;
    }

    private AvailableSlotsDto toAvailableSlotDto(Doctor_time_slots slot) {
        AvailableSlotsDto dto = new AvailableSlotsDto();
        dto.setSlotId(slot.getId());
        dto.setDayOfWeek(slot.getDayOfWeek());
        dto.setTotalSeats(slot.getTotalSeats());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        dto.setTimePeriod(slot.getStartTime().format(fmt) + " - " + slot.getEndTime().format(fmt));

        return dto;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<ViewDoctorsDto>> viewDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<ViewDoctorsDto> doctorsList = doctors.stream()
                .map(this::toViewDoctorsDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctorsList);
    }

    private ViewDoctorsDto toViewDoctorsDto(Doctor doctor) {
        ViewDoctorsDto dto = new ViewDoctorsDto();
        dto.setImage(doctor.getImage());
        dto.setDoctorId(doctor.getDoctorId());
        dto.setName(doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName());
        dto.setSpecialization(doctor.getSpecialization());
        return dto;
    }

    // Existing: Paged search by name or specialization
    @Transactional(readOnly = true)
    public ResponseEntity<PagedResponse<ViewDoctorsDto>> searchDoctors(
            String q, String specialization, String district, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "doctorId"));
        Page<Doctor> result = doctorRepository.searchByNameOrSpecialization(
                emptyToNull(q), emptyToNull(specialization), pageable);

        List<ViewDoctorsDto> content = result.getContent().stream()
                .map(this::toViewDoctorsDto)
                .collect(Collectors.toList());

        PagedResponse<ViewDoctorsDto> response = new PagedResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
        return ResponseEntity.ok(response);
    }

    // Existing: Non-paged list (returns same shape as /viewAll)
    @Transactional(readOnly = true)
    public ResponseEntity<List<ViewDoctorsDto>> searchDoctorsList(String q, String specialization) {
        List<Doctor> docs = doctorRepository.searchListByNameOrSpecialization(
                emptyToNull(q), emptyToNull(specialization));
        List<ViewDoctorsDto> out = docs.stream()
                .map(this::toViewDoctorsDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    // NEW: Paged search with hospital filter
    @Transactional(readOnly = true)
    public ResponseEntity<PagedResponse<ViewDoctorsDto>> searchDoctorsByHospital(
            String q, String specialization, Long hospitalId, String hospitalName,
            int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "doctorId"));
        Page<Doctor> result = doctorRepository.searchByNameSpecAndHospital(
                emptyToNull(q), emptyToNull(specialization), hospitalId, emptyToNull(hospitalName), pageable);

        List<ViewDoctorsDto> content = result.getContent().stream()
                .map(this::toViewDoctorsDto)
                .collect(Collectors.toList());

        PagedResponse<ViewDoctorsDto> response = new PagedResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
        return ResponseEntity.ok(response);
    }

    // NEW: Non-paged list with hospital filter (same shape as /viewAll)
    @Transactional(readOnly = true)
    public ResponseEntity<List<ViewDoctorsDto>> searchDoctorsByHospitalList(
            String q, String specialization, Long hospitalId, String hospitalName) {

        List<Doctor> docs = doctorRepository.searchListByNameSpecAndHospital(
                emptyToNull(q), emptyToNull(specialization), hospitalId, emptyToNull(hospitalName));

        List<ViewDoctorsDto> out = docs.stream()
                .map(this::toViewDoctorsDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<String>> getSpecializations() {
        return ResponseEntity.ok(doctorRepository.findAllSpecializations());
    }

    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}