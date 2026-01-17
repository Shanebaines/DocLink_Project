package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.*;
import com.springbootpractice.doclink.Kernal.Entity.*;
import com.springbootpractice.doclink.Kernal.Enums.PrescriptionStatusType;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_time_slots;
import com.springbootpractice.doclink.Kernal.Relations.Doctors_in_Hospital;
import com.springbootpractice.doclink.Kernal.Relations.MedicalRecord;
import com.springbootpractice.doclink.Listner.Dto.Request.CreateMedicalRecordDto;
import com.springbootpractice.doclink.Listner.Dto.Request.CreateMedicalReportDto;
import com.springbootpractice.doclink.Listner.Dto.Request.CreatePrescriptionDto;
import com.springbootpractice.doclink.Listner.Dto.Response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.ArrayList;

import com.springbootpractice.doclink.Listner.Dto.Request.PrescriptionMedicationDto;
import com.springbootpractice.doclink.Listner.Dto.Response.PrescriptionResponseDto;
import com.springbootpractice.doclink.Listner.Dto.Response.PrescriptionMedicationResponseDto;
import com.springbootpractice.doclink.Kernal.Entity.PrescriptionMedication;

import com.springbootpractice.doclink.Kernal.Util.RatingUtils;

import java.time.LocalDateTime;
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

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;

    @Autowired  // <--- ADD THIS
    private FeedbackRepository feedbackRepository;

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMedicationRepository prescriptionMedicationRepository;
    private final MedicationRepository medicationRepository;

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

        List<WorkPLaceDto> workPlaces = viewWorkPlaces(id).getBody();

        dto.setWorkPlaces(workPlaces);
        return ResponseEntity.ok(dto);
    }
    public ResponseEntity<List<WorkPLaceDto>> viewWorkPlaces(Long id) {
        List<Doctors_in_Hospital> places = doctorsInHospitalRepository.findAllByDoctorIdWithHospital(Long.valueOf(id));
        List<WorkPLaceDto> workPlaces = places.stream()
                .map(dih -> toWorkPlaceDto(dih, id))
                .collect(Collectors.toList());
        return ResponseEntity.ok(workPlaces);
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

    public void updateDoctorRating(String doctorId) {
        // 1. Fetch all ratings (Convert ID to Long here)
        // NOTE: Ensure your FeedbackRepository has a method findRatingsByDoctorId(Long id)
        List<Integer> ratingList = feedbackRepository.findRatingsByDoctorId(Long.parseLong(doctorId));

        // 2. Use the Util function
        double newAverage = RatingUtils.calculateAverageRating(ratingList);

        // 3. Save the new average to the Doctor entity
        // Convert ID to Long here as well
        Doctor doctor = doctorRepository.findById(Long.parseLong(doctorId)).orElse(null);

        if (doctor != null) {
            doctor.setAverageRating(newAverage); // This will work after you update Doctor.java
            doctorRepository.save(doctor);
        }
    }

    @Transactional
    public ResponseEntity<MedicalRecordResponseDto> createMedicalRecord(CreateMedicalRecordDto createDto) {

        // Validations
        // At least one of medicalReport or prescription must be present
        boolean hasMedicalReport = createDto.getMedicalReport() != null;
        boolean hasPrescription = createDto.getPrescription() != null;

        if (!hasMedicalReport && !hasPrescription) {
            log.error("createMedicalRecord: both medicalReport and prescription are null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Validate patient
        Optional<Patient> optPatient = patientRepository.findById(createDto.getPatientId());
        if (optPatient.isEmpty()) {
            log.error("Patient not found with ID: {}", createDto.getPatientId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Patient patient = optPatient.get();

        // Validate doctor
        Optional<Doctor> optDoctor = doctorRepository.findById(createDto.getDoctorId());
        if (optDoctor.isEmpty()) {
            log.error("Doctor not found with ID: {}", createDto.getDoctorId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Doctor doctor = optDoctor.get();

        // If there is a prescription section, validate medications first
        if (hasPrescription) {
            CreatePrescriptionDto rxDto = createDto.getPrescription();

            if (rxDto.getMedications() == null || rxDto.getMedications().isEmpty()) {
                log.error("No medications provided in embedded prescription");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            for (PrescriptionMedicationDto medDto : rxDto.getMedications()) {
                if (medDto.getMedicationId() == null) {
                    log.error("Medication ID is null in embedded prescription");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
                if (medicationRepository.findById(medDto.getMedicationId()).isEmpty()) {
                    log.error("Medication not found with ID: {}", medDto.getMedicationId());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            }
        }




        // Build MedicalRecord (not yet persisted)
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setPatient(patient);
        medicalRecord.setDoctor(doctor);
        medicalRecord.setHasMedicalReport(hasMedicalReport);
        medicalRecord.setHasPrescription(hasPrescription);

        // Build MedicalReport entity if provided
        if (hasMedicalReport) {
            CreateMedicalReportDto reportDto = createDto.getMedicalReport();

            MedicalReport medicalReport = new MedicalReport();
            medicalReport.setMedicalRecord(medicalRecord);   // @MapsId
            medicalReport.setVisitDate(reportDto.getVisitDate());
            medicalReport.setSymptoms(reportDto.getSymptoms());
            medicalReport.setDiagnosis(reportDto.getDiagnosis());
            medicalReport.setTreatment(reportDto.getTreatment());
            medicalReport.setRecommendation(reportDto.getRecommendation());
            medicalReport.setVitalSigns(reportDto.getVitalSigns());

            medicalRecord.setMedicalReport(medicalReport);
        }

        // Persist MedicalRecord (cascades to MedicalReport)
        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);
        log.info("Medical record created with ID: {}", savedRecord.getMedicalRecordId());

        // If prescription data was provided, create prescription linked to this record
        PrescriptionResponseDto prescriptionDto = null;
        if (hasPrescription) {
            prescriptionDto = createPrescriptionForRecord(savedRecord, createDto.getPrescription());
        }

        // Build MedicalReportResponseDto (if present)
        MedicalReportResponseDto medicalReportResponse = null;
        if (hasMedicalReport) {
            MedicalReport mr = savedRecord.getMedicalReport();
            medicalReportResponse = MedicalReportResponseDto.builder()
                    .reportId(mr.getReportId())
                    .visitDate(mr.getVisitDate())
                    .symptoms(mr.getSymptoms())
                    .diagnosis(mr.getDiagnosis())
                    .treatment(mr.getTreatment())
                    .recommendation(mr.getRecommendation())
                    .vitalSigns(mr.getVitalSigns())
                    .createdAt(mr.getCreatedAt())
                    .updatedAt(mr.getUpdatedAt())
                    .build();
        }

        String patientName = patient.getUser().getFirstName() + " " + patient.getUser().getLastName();
        String doctorName = doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName();

        MedicalRecordResponseDto response = MedicalRecordResponseDto.builder()
                .medicalRecordId(savedRecord.getMedicalRecordId())
                .patientId(patient.getPatientId())
                .patientName(patientName)
                .doctorId(doctor.getDoctorId())
                .doctorName(doctorName)
                .hasMedicalReport(hasMedicalReport)
                .hasPrescription(hasPrescription)
                .medicalReport(medicalReportResponse)
                .prescription(prescriptionDto)
                .createdAt(savedRecord.getCreatedAt())
                .updatedAt(savedRecord.getUpdatedAt())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    private PrescriptionResponseDto createPrescriptionForRecord(
            MedicalRecord medicalRecord,
            CreatePrescriptionDto prescriptionDto) {

        LocalDateTime now = LocalDateTime.now();

        // Create Prescription linked to the MedicalRecord (and, if present, patient/doctor)
        Prescription prescription = new Prescription();
        prescription.setMedicalRecord(medicalRecord);


        prescription.setPrescriptionDate(
                prescriptionDto.getPrescriptionDate() != null
                        ? prescriptionDto.getPrescriptionDate()
                        : LocalDate.now()
        );
        prescription.setDiagnosis(prescriptionDto.getDiagnosis());
        prescription.setInstructions(prescriptionDto.getInstructions());
        prescription.setStatus(PrescriptionStatusType.active);
        prescription.setCreatedAt(now);
        prescription.setUpdatedAt(now);

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        log.info("Prescription created with ID: {}", savedPrescription.getPrescriptionId());

        // Create prescription medications
        List<PrescriptionMedication> prescriptionMedications = new ArrayList<>();
        for (PrescriptionMedicationDto medDto : prescriptionDto.getMedications()) {
            Medication medication = medicationRepository
                    .findById(medDto.getMedicationId())
                    .orElseThrow(); // already validated in createMedicalRecord

            PrescriptionMedication prescriptionMedication = new PrescriptionMedication();
            prescriptionMedication.setPrescription(savedPrescription);
            prescriptionMedication.setMedication(medication);
            prescriptionMedication.setDosage(medDto.getDosage());
            prescriptionMedication.setFrequency(medDto.getFrequency());
            prescriptionMedication.setDurationDays(medDto.getDurationDays());
            prescriptionMedication.setQuantity(medDto.getQuantity());
            prescriptionMedication.setInstructions(medDto.getInstructions());
            prescriptionMedication.setCreatedAt(now);

            prescriptionMedications.add(prescriptionMedication);
        }

        List<PrescriptionMedication> savedMedications =
                prescriptionMedicationRepository.saveAll(prescriptionMedications);

        log.info("Saved {} medications for prescription ID: {}",
                savedMedications.size(), savedPrescription.getPrescriptionId());

        // Map medications to response DTOs
        List<PrescriptionMedicationResponseDto> medicationResponses = new ArrayList<>();
        for (PrescriptionMedication pm : savedMedications) {
            PrescriptionMedicationResponseDto medResponse = PrescriptionMedicationResponseDto.builder()
                    .prescriptionMedicationId(pm.getPrescriptionMedicationId())
                    .medicationId(pm.getMedication().getMedicationId())
                    .medicationName(pm.getMedication().getMedicationName())
                    .dosage(pm.getDosage())
                    .frequency(pm.getFrequency())
                    .durationDays(pm.getDurationDays())
                    .quantity(pm.getQuantity())
                    .instructions(pm.getInstructions())
                    .build();
            medicationResponses.add(medResponse);
        }

        // Build and return prescription DTO
        return PrescriptionResponseDto.builder()
                .prescriptionId(savedPrescription.getPrescriptionId())
                .prescriptionDate(savedPrescription.getPrescriptionDate())
                .diagnosis(savedPrescription.getDiagnosis())
                .instructions(savedPrescription.getInstructions())
                .status(savedPrescription.getStatus())
                .medications(medicationResponses)
                .createdAt(savedPrescription.getCreatedAt())
                .updatedAt(savedPrescription.getUpdatedAt())
                .build();
    }


    @Transactional
    public ResponseEntity<PrescriptionResponseDto> createPrescription(
            Long patientId,
            Long doctorId,
            CreatePrescriptionDto request) {

        log.info("Creating prescription for patient ID: {} by doctor ID: {}", patientId, doctorId);

        // Validate patient
        Optional<Patient> optPatient = patientRepository.findById(patientId);
        if (optPatient.isEmpty()) {
            log.error("Patient not found with ID: {}", patientId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Patient patient = optPatient.get();

        // Validate doctor
        Optional<Doctor> optDoctor = doctorRepository.findById(doctorId);
        if (optDoctor.isEmpty()) {
            log.error("Doctor not found with ID: {}", doctorId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Doctor doctor = optDoctor.get();

        // Validate medications
        if (request.getMedications() == null || request.getMedications().isEmpty()) {
            log.error("No medications provided for prescription");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        for (PrescriptionMedicationDto medDto : request.getMedications()) {
            if (medDto.getMedicationId() == null) {
                log.error("Medication ID is null in prescription request");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            if (medicationRepository.findById(medDto.getMedicationId()).isEmpty()) {
                log.error("Medication not found with ID: {}", medDto.getMedicationId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        // Create a MedicalRecord for this prescription
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setPatient(patient);
        medicalRecord.setDoctor(doctor);
        medicalRecord.setHasMedicalReport(false);
        medicalRecord.setHasPrescription(true);

        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);
        log.info("Medical record created for prescription with ID: {}", savedRecord.getMedicalRecordId());

        // Delegate to helper that creates Prescription + medications for this record
        PrescriptionResponseDto responseDto = createPrescriptionForRecord(savedRecord, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

}