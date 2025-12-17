package com.springbootpractice.doclink.Kernel.Service;

import com.springbootpractice.doclink.Dealer.*;
import com.springbootpractice.doclink.Kernel.Entity.*;
import com.springbootpractice.doclink.Kernel.Enums.PrescriptionStatusType;
import com.springbootpractice.doclink.Kernel.Relations.MedicalRecord;
import com.springbootpractice.doclink.Kernel.Relations.PrescriptionToken;
import com.springbootpractice.doclink.Kernel.Util.TokenGenerator;
import com.springbootpractice.doclink.Listner.Dto.Request.*;
import com.springbootpractice.doclink.Listner.Dto.Response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalReportRepository medicalReportRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicationRepository medicationRepository;
    private final PrescriptionMedicationRepository prescriptionMedicationRepository;
    private final PrescriptionTokenRepository prescriptionTokenRepository;

    /**
     * Creates a medical record with optional medical report and/or prescription
     */
    @Transactional
    public ResponseEntity<MedicalRecordResponseDto> createMedicalRecord(CreateMedicalRecordDto request) {
        log.info("Creating medical record for patient ID: {} by doctor ID: {}",
                request.getPatientId(), request.getDoctorId());

        // Validate at least one type is provided
        if (request.getMedicalReport() == null && request.getPrescription() == null) {
            log.error("At least medical report or prescription must be provided");
            return ResponseEntity.badRequest().build();
        }

        // Validate patient exists
        Optional<Patient> optPatient = patientRepository.findById(request.getPatientId());
        if (optPatient.isEmpty()) {
            log.error("Patient not found with ID: {}", request.getPatientId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Patient patient = optPatient.get();

        // Validate doctor exists
        Optional<Doctor> optDoctor = doctorRepository.findById(request.getDoctorId());
        if (optDoctor.isEmpty()) {
            log.error("Doctor not found with ID: {}", request.getDoctorId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Doctor doctor = optDoctor.get();

        // Create and save the parent MedicalRecord first
        MedicalRecord medicalRecord = MedicalRecord.builder()
                .patient(patient)
                .doctor(doctor)
                .hasMedicalReport(request.getMedicalReport() != null)
                .hasPrescription(request.getPrescription() != null)
                .build();

        MedicalRecord savedMedicalRecord = medicalRecordRepository.save(medicalRecord);
        log.info("Medical record created with ID: {}", savedMedicalRecord.getMedicalRecordId());

        // Create MedicalReport if provided
        MedicalReport savedMedicalReport = null;
        if (request.getMedicalReport() != null) {
            savedMedicalReport = createMedicalReportEntity(
                    request.getMedicalReport(),
                    savedMedicalRecord
            );
            savedMedicalRecord.setMedicalReport(savedMedicalReport);
            log.info("Medical report created with ID: {}", savedMedicalReport.getReportId());
        }

        // Create Prescription if provided
        Prescription savedPrescription = null;
        if (request.getPrescription() != null) {
            ResponseEntity<Prescription> prescriptionResult = createPrescriptionEntity(
                    request.getPrescription(),
                    savedMedicalRecord
            );
            if (prescriptionResult.getStatusCode() != HttpStatus.OK) {
                // Rollback will happen due to @Transactional
                return ResponseEntity.status(prescriptionResult.getStatusCode()).build();
            }
            savedPrescription = prescriptionResult.getBody();
            savedMedicalRecord.setPrescription(savedPrescription);
            log.info("Prescription created with ID: {}", savedPrescription.getPrescriptionId());
        }

        // Build and return response
        MedicalRecordResponseDto response = buildMedicalRecordResponse(
                savedMedicalRecord,
                savedMedicalReport,
                savedPrescription
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Creates only a medical report (creates parent MedicalRecord automatically)
     */
    @Transactional
    public ResponseEntity<MedicalRecordResponseDto> createMedicalReportOnly(
            Long patientId,
            Long doctorId,
            CreateMedicalReportDto reportDto) {

        CreateMedicalRecordDto request = new CreateMedicalRecordDto();
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setMedicalReport(reportDto);
        request.setPrescription(null);

        return createMedicalRecord(request);
    }

    /**
     * Creates only a prescription (creates parent MedicalRecord automatically)
     */
    @Transactional
    public ResponseEntity<MedicalRecordResponseDto> createPrescriptionOnly(
            Long patientId,
            Long doctorId,
            CreatePrescriptionDto prescriptionDto) {

        CreateMedicalRecordDto request = new CreateMedicalRecordDto();
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setMedicalReport(null);
        request.setPrescription(prescriptionDto);

        return createMedicalRecord(request);
    }

    /**
     * Get medical record by ID with all details
     */
    @Transactional(readOnly = true)
    public ResponseEntity<MedicalRecordResponseDto> getMedicalRecordById(Long id) {
        Optional<MedicalRecord> optRecord = medicalRecordRepository.findByIdWithDetails(id);

        if (optRecord.isEmpty()) {
            log.error("Medical record not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }

        MedicalRecord record = optRecord.get();
        MedicalRecordResponseDto response = buildMedicalRecordResponse(
                record,
                record.getMedicalReport(),
                record.getPrescription()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get all medical records for a patient
     */
    @Transactional(readOnly = true)
    public ResponseEntity<List<MedicalRecordResponseDto>> getMedicalRecordsByPatientId(Long patientId) {
        List<MedicalRecord> records = medicalRecordRepository
                .findByPatientPatientIdOrderByCreatedAtDesc(patientId);

        List<MedicalRecordResponseDto> responses = records.stream()
                .map(record -> buildMedicalRecordResponse(
                        record,
                        record.getMedicalReport(),
                        record.getPrescription()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // ==================== Private Helper Methods ====================

    private MedicalReport createMedicalReportEntity(
            CreateMedicalReportDto dto,
            MedicalRecord medicalRecord) {

        MedicalReport report = MedicalReport.builder()
                .medicalRecord(medicalRecord)
                .visitDate(dto.getVisitDate())
                .symptoms(dto.getSymptoms())
                .diagnosis(dto.getDiagnosis())
                .treatment(dto.getTreatment())
                .recommendation(dto.getRecommendation())
                .vitalSigns(dto.getVitalSigns())
                .build();

        return medicalReportRepository.save(report);
    }

    private ResponseEntity<Prescription> createPrescriptionEntity(
            CreatePrescriptionDto dto,
            MedicalRecord medicalRecord) {

        // Validate all medications exist before creating prescription
        for (PrescriptionMedicationDto medDto : dto.getMedications()) {
            if (!medicationRepository.existsById(medDto.getMedicationId())) {
                log.error("Medication not found with ID: {}", medDto.getMedicationId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        Prescription prescription = Prescription.builder()
                .medicalRecord(medicalRecord)
                .prescriptionDate(dto.getPrescriptionDate() != null
                        ? dto.getPrescriptionDate()
                        : LocalDate.now())
                .diagnosis(dto.getDiagnosis())
                .instructions(dto.getInstructions())
                .status(PrescriptionStatusType.active)
                .prescriptionMedications(new ArrayList<>())
                .build();

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        // Create prescription medications
        for (PrescriptionMedicationDto medDto : dto.getMedications()) {
            Medication medication = medicationRepository.findById(medDto.getMedicationId()).get();

            PrescriptionMedication prescriptionMedication = PrescriptionMedication.builder()
                    .prescription(savedPrescription)
                    .medication(medication)
                    .dosage(medDto.getDosage())
                    .frequency(medDto.getFrequency())
                    .durationDays(medDto.getDurationDays())
                    .quantity(medDto.getQuantity())
                    .instructions(medDto.getInstructions())
                    .build();

            prescriptionMedicationRepository.save(prescriptionMedication);
            savedPrescription.getPrescriptionMedications().add(prescriptionMedication);
        }

        // Generate and store token
        String tokenValue = TokenGenerator.generateReadableToken();
        PrescriptionToken token = PrescriptionToken.builder()
                .token(tokenValue)
                .prescription(savedPrescription)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .active(true)
                .build();

        prescriptionTokenRepository.save(token);

        log.info("Generated token '{}' for prescription ID: {}", tokenValue, savedPrescription.getPrescriptionId());

        return ResponseEntity.ok(savedPrescription);
    }

    private MedicalRecordResponseDto buildMedicalRecordResponse(
            MedicalRecord medicalRecord,
            MedicalReport medicalReport,
            Prescription prescription) {

        // Build patient name
        String patientName = medicalRecord.getPatient().getUser().getFirstName() + " " +
                medicalRecord.getPatient().getUser().getLastName();

        // Build doctor name
        String doctorName = medicalRecord.getDoctor().getUser().getFirstName() + " " +
                medicalRecord.getDoctor().getUser().getLastName();

        return MedicalRecordResponseDto.builder()
                .medicalRecordId(medicalRecord.getMedicalRecordId())
                .patientId(medicalRecord.getPatient().getPatientId())
                .patientName(patientName)
                .doctorId(medicalRecord.getDoctor().getDoctorId())
                .doctorName(doctorName)
                .hasMedicalReport(medicalRecord.getHasMedicalReport())
                .hasPrescription(medicalRecord.getHasPrescription())
                .medicalReport(medicalReport != null ? buildMedicalReportResponse(medicalReport) : null)
                .prescription(prescription != null ? buildPrescriptionResponse(prescription) : null)
                .createdAt(medicalRecord.getCreatedAt())
                .updatedAt(medicalRecord.getUpdatedAt())
                .build();
    }

    private MedicalReportResponseDto buildMedicalReportResponse(MedicalReport report) {
        return MedicalReportResponseDto.builder()
                .reportId(report.getReportId())
                .visitDate(report.getVisitDate())
                .symptoms(report.getSymptoms())
                .diagnosis(report.getDiagnosis())
                .treatment(report.getTreatment())
                .recommendation(report.getRecommendation())
                .vitalSigns(report.getVitalSigns())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }

    private PrescriptionResponseDto buildPrescriptionResponse(Prescription prescription) {
        List<PrescriptionMedicationResponseDto> medicationResponses = prescription
                .getPrescriptionMedications()
                .stream()
                .map(this::buildPrescriptionMedicationResponse)
                .collect(Collectors.toList());

        return PrescriptionResponseDto.builder()
                .prescriptionId(prescription.getPrescriptionId())
                .prescriptionDate(prescription.getPrescriptionDate())
                .diagnosis(prescription.getDiagnosis())
                .instructions(prescription.getInstructions())
                .status(prescription.getStatus())
                .medications(medicationResponses)
                .createdAt(prescription.getCreatedAt())
                .updatedAt(prescription.getUpdatedAt())
                .build();
    }

    private PrescriptionMedicationResponseDto buildPrescriptionMedicationResponse(
            PrescriptionMedication pm) {
        return PrescriptionMedicationResponseDto.builder()
                .prescriptionMedicationId(pm.getPrescriptionMedicationId())
                .medicationId(pm.getMedication().getMedicationId())
                .medicationName(pm.getMedication().getMedicationName())
                .dosage(pm.getDosage())
                .frequency(pm.getFrequency())
                .durationDays(pm.getDurationDays())
                .quantity(pm.getQuantity())
                .instructions(pm.getInstructions())
                .build();
    }
}