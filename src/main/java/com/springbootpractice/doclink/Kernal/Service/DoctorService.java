package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.*;
import com.springbootpractice.doclink.Kernal.Entity.*;
import com.springbootpractice.doclink.Kernal.Enums.PrescriptionStatusType;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_time_slots;
import com.springbootpractice.doclink.Kernal.Relations.Doctors_in_Hospital;
import com.springbootpractice.doclink.Listner.Dto.Request.CreateMedicalRecordDto;
import com.springbootpractice.doclink.Listner.Dto.Request.CreatePrescriptionRequest;
import com.springbootpractice.doclink.Listner.Dto.Request.PrescriptionMedicationDTO;
import com.springbootpractice.doclink.Listner.Dto.Response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// Check your package name in RatingUtils.java first!
// It should likely be: package com.springbootpractice.doclink.Util;

import com.springbootpractice.doclink.Kernal.Util.RatingUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springbootpractice.doclink.Dealer.DoctorRepository;
import com.springbootpractice.doclink.Dealer.DoctorTimeSlotRepository;
import com.springbootpractice.doclink.Dealer.DoctorsInHospitalRepository;
import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import com.springbootpractice.doclink.Kernal.Entity.Hospital;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_time_slots;
import com.springbootpractice.doclink.Kernal.Relations.Doctors_in_Hospital;
import com.springbootpractice.doclink.Listner.Dto.Response.AvailableSlotsDto;
import com.springbootpractice.doclink.Listner.Dto.Response.PagedResponse;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewDoctorDto;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewDoctorsDto;
import com.springbootpractice.doclink.Listner.Dto.Response.WorkPLaceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        dto.setAverageRating(doctor.getAverageRating());

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

        @Transactional(readOnly = true)
        public ResponseEntity<List<AvailableSlotsDto>> slotsForAuthenticatedDoctor(Principal principal, Long hospitalId, String hospitalName) {
                if (principal == null || principal.getName() == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }

                Optional<Doctor> optDoctor = doctorRepository.findByUserUsername(principal.getName());
                if (optDoctor.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }

                Long doctorId = optDoctor.get().getDoctorId();
                return slotsByHospital(doctorId, hospitalId, hospitalName);
        }

        @Transactional(readOnly = true)
        public ResponseEntity<List<AvailableSlotsDto>> slotsByHospital(Long doctorId, Long hospitalId, String hospitalName) {
                List<Doctor_time_slots> timeSlots;

                if (hospitalId != null) {
                        timeSlots = doctorTimeSlotRepository.findByDoctorDoctorIdAndHospitalHospitalIdOrderByDayOfWeekAscStartTimeAsc(
                                        doctorId, hospitalId);
                } else if (hospitalName != null && !hospitalName.trim().isEmpty()) {
                        timeSlots = doctorTimeSlotRepository.findByDoctorDoctorIdAndHospitalHospitalNameOrderByDayOfWeekAscStartTimeAsc(
                                        doctorId, hospitalName.trim());
                } else {
                        return ResponseEntity.badRequest().build();
                }

                List<AvailableSlotsDto> slotDtos = timeSlots.stream()
                                .map(this::toAvailableSlotDto)
                                .collect(Collectors.toList());

                return ResponseEntity.ok(slotDtos);
        }

    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
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
//        record.setNotes(createDto.getNotes());
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
//        dto.setNotes(record.getNotes());
        dto.setVitalSigns(record.getVitalSigns());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());
        return dto;
    }


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

}