package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.*;
import com.springbootpractice.doclink.Kernal.Entity.*;
import com.springbootpractice.doclink.Kernal.Enums.PrescriptionStatusType;
import com.springbootpractice.doclink.Listner.Dto.Request.CreateMedicalRecordDto;
import com.springbootpractice.doclink.Listner.Dto.Request.CreatePrescriptionRequest;
import com.springbootpractice.doclink.Listner.Dto.Request.PrescriptionMedicationDTO;
import com.springbootpractice.doclink.Listner.Dto.Response.MedicalRecordDto;
import com.springbootpractice.doclink.Listner.Dto.Response.PrescriptionMedicationResponse;
import com.springbootpractice.doclink.Listner.Dto.Response.PrescriptionResponse;
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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MedicalRecordService {
    public final PatientRepository patientRepository;
    public final DoctorRepository doctorRepository;
    public final PrescriptionRepository prescriptionRepository;
    public final MedicalRecordRepository medicalRecordRepository;
    public final MedicationRepository medicationRepository;
    public final PrescriptionMedicationRepository prescriptionMedicationRepository;

    @Transactional
    public ResponseEntity<PrescriptionResponse> createPrescription(CreatePrescriptionRequest request) {
        log.info("Creating prescription for patient ID: {} by doctor ID: {}",
                request.getPatientId(), request.getDoctorId());

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

        // Create and save prescription
        Prescription prescription = new Prescription();
        prescription.setPatient(patient);
        prescription.setDoctor(doctor);

        // Set prescription date
        prescription.setPrescriptionDate(
                request.getPrescriptionDate() != null
                        ? request.getPrescriptionDate()
                        : LocalDate.now()
        );

        prescription.setDiagnosis(request.getDiagnosis());
        prescription.setInstructions(request.getInstructions());
        prescription.setStatus(PrescriptionStatusType.active);

        LocalDateTime now = LocalDateTime.now();
        prescription.setCreatedAt(now);
        prescription.setUpdatedAt(now);

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        log.info("Prescription saved with ID: {}", savedPrescription.getPrescriptionId());

        // Create and save prescription medications
        List<PrescriptionMedication> prescriptionMedications = new ArrayList<>();

        for (PrescriptionMedicationDTO medDto : request.getMedications()) {
            // Validate medication exists
            Optional<Medication> optMedication = medicationRepository.findById(medDto.getMedicationId());
            if (optMedication.isEmpty()) {
                log.error("Medication not found with ID: {}", medDto.getMedicationId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Medication medication = optMedication.get();

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

        // Build and return response
        PrescriptionResponse response = buildPrescriptionResponse(savedPrescription, savedMedications);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private PrescriptionResponse buildPrescriptionResponse(
            Prescription prescription,
            List<PrescriptionMedication> medications) {

        PrescriptionResponse response = new PrescriptionResponse();
        response.setPrescriptionId(prescription.getPrescriptionId());
        response.setPatientId(prescription.getPatient().getPatientId());

        // Build patient name
        String patientFirstName = prescription.getPatient().getUser().getFirstName();
        String patientLastName = prescription.getPatient().getUser().getLastName();
        response.setPatientName(patientFirstName + " " + patientLastName);

        response.setDoctorId(prescription.getDoctor().getDoctorId());

        // Build doctor name
        String doctorFirstName = prescription.getDoctor().getUser().getFirstName();
        String doctorLastName = prescription.getDoctor().getUser().getLastName();
        response.setDoctorName(doctorFirstName + " " + doctorLastName);

        response.setPrescriptionDate(prescription.getPrescriptionDate());
        response.setDiagnosis(prescription.getDiagnosis());
        response.setInstructions(prescription.getInstructions());
        response.setStatus(prescription.getStatus());

        // Build medication responses
        List<PrescriptionMedicationResponse> medicationResponses = new ArrayList<>();
        for (PrescriptionMedication pm : medications) {
            PrescriptionMedicationResponse medResponse = new PrescriptionMedicationResponse();
            medResponse.setPrescriptionMedicationId(pm.getPrescriptionMedicationId());
            medResponse.setMedicationId(pm.getMedication().getMedicationId());
            medResponse.setMedicationName(pm.getMedication().getMedicationName());
            medResponse.setDosage(pm.getDosage());
            medResponse.setFrequency(pm.getFrequency());
            medResponse.setDurationDays(pm.getDurationDays());
            medResponse.setQuantity(pm.getQuantity());
            medResponse.setInstructions(pm.getInstructions());
            medicationResponses.add(medResponse);
        }

        response.setMedications(medicationResponses);
        return response;
    }

    @Transactional
    public ResponseEntity<MedicalRecordDto> createMedicalRecord(CreateMedicalRecordDto createDto) {

        // Check if patient exists
        Optional<Patient> optPatient = patientRepository.findById(createDto.getPatientId());
        if (optPatient.isEmpty()) {
            log.error("Patient not found with ID: {}", createDto.getPatientId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Check if doctor exists
        Optional<Doctor> optDoctor = doctorRepository.findById(createDto.getDoctorId());
        if (optDoctor.isEmpty()) {
            log.error("Doctor not found with ID: {}", createDto.getDoctorId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Create new MedicalRecord entity
        MedicalRecord record = new MedicalRecord();
        record.setPatient(optPatient.get());
        record.setDoctor(optDoctor.get());
        record.setVisitDate(createDto.getVisitDate());
        record.setSymptoms(createDto.getSymptoms());
        record.setDiagnosis(createDto.getDiagnosis());
        record.setTreatment(createDto.getTreatment());
        record.setVitalSigns(createDto.getVitalSigns());

        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);

        // Save to database
        MedicalRecord savedRecord = medicalRecordRepository.save(record);

        log.info("Medical record created with ID: {}", savedRecord.getRecordId());

        // Convert entity to DTO and return
        MedicalRecordDto responseDto = toMedicalRecordDto(savedRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    private MedicalRecordDto toMedicalRecordDto(MedicalRecord record) {
        MedicalRecordDto dto = new MedicalRecordDto();
        dto.setRecordId(record.getRecordId());
        dto.setPatientId(record.getPatient().getPatientId());
        dto.setDoctorId(record.getDoctor() != null ? record.getDoctor().getDoctorId() : null);
        dto.setVisitDate(record.getVisitDate());
        dto.setSymptoms(record.getSymptoms());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setTreatment(record.getTreatment());
        dto.setVitalSigns(record.getVitalSigns());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());
        return dto;
    }
}
