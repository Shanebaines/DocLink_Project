package com.springbootpractice.doclink.Kernal.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springbootpractice.doclink.Dealer.DoctorRepository;
import com.springbootpractice.doclink.Dealer.PatientDoctorBookmarkRepository;
import com.springbootpractice.doclink.Dealer.PatientRepository;
import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import com.springbootpractice.doclink.Kernal.Entity.Patient;
import com.springbootpractice.doclink.Kernal.Relations.PatientDoctorBookmark;
import com.springbootpractice.doclink.Listner.Dto.Response.BookmarkDoctorDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookmarkService {

    private final PatientDoctorBookmarkRepository bookmarkRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    public ResponseEntity<String> addBookmark(Long patientId, Long doctorId) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isEmpty()) {
            log.warn("Patient not found when adding bookmark: {}", patientId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
        }

        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            log.warn("Doctor not found when adding bookmark: {}", doctorId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found");
        }

        boolean exists = bookmarkRepository.existsByPatientPatientIdAndDoctorDoctorId(patientId, doctorId);
        if (exists) {
            log.info("Bookmark already exists for patient {} and doctor {}", patientId, doctorId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Bookmark already exists");
        }

        PatientDoctorBookmark bookmark = new PatientDoctorBookmark();
        bookmark.setPatient(patientOpt.get());
        bookmark.setDoctor(doctorOpt.get());
        bookmarkRepository.save(bookmark);

        log.info("Created bookmark for patient {} and doctor {}", patientId, doctorId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Bookmark added successfully");
    }

    @Transactional
    public ResponseEntity<String> removeBookmark(Long patientId, Long doctorId) {
        Optional<PatientDoctorBookmark> bookmarkOpt =
                bookmarkRepository.findByPatientPatientIdAndDoctorDoctorId(patientId, doctorId);

        if (bookmarkOpt.isEmpty()) {
            log.warn("Bookmark not found for patient {} and doctor {}", patientId, doctorId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bookmark not found");
        }

        bookmarkRepository.delete(bookmarkOpt.get());
        log.info("Removed bookmark for patient {} and doctor {}", patientId, doctorId);
        return ResponseEntity.ok("Bookmark removed successfully");
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<BookmarkDoctorDto>> listBookmarks(Long patientId) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isEmpty()) {
            log.warn("Patient not found when listing bookmarks: {}", patientId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<PatientDoctorBookmark> bookmarks =
                bookmarkRepository.findByPatientPatientIdOrderByCreatedAtDesc(patientId);

        List<BookmarkDoctorDto> dtos = bookmarks.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        if (dtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(dtos);
    }

    private BookmarkDoctorDto toDto(PatientDoctorBookmark bookmark) {
        Doctor doctor = bookmark.getDoctor();
        BookmarkDoctorDto dto = new BookmarkDoctorDto();
        dto.setDoctorId(doctor.getDoctorId());
        dto.setName(doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        dto.setYearsExperience(doctor.getYearsExperience());
        dto.setQualification(doctor.getQualification());
        dto.setEmail(doctor.getUser().getEmail());
        dto.setPhoneNumber(doctor.getUser().getPhoneNumber());
        dto.setImage(doctor.getImage());
        return dto;
    }
}
