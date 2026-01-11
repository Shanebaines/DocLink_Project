package com.springbootpractice.doclink.Kernal.Service;

import com.springbootpractice.doclink.Dealer.DoctorRepository;
import com.springbootpractice.doclink.Dealer.FeedbackRepository;
import com.springbootpractice.doclink.Dealer.PatientRepository;
import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import com.springbootpractice.doclink.Kernal.Entity.Feedback;
import com.springbootpractice.doclink.Kernal.Entity.Patient;
import com.springbootpractice.doclink.Listner.Dto.Request.CreateFeedbackDto;
import com.springbootpractice.doclink.Listner.Dto.Response.FeedbackViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    private DoctorService doctorService;

    /**
     * Submit new feedback (Supports optional Rating or Comment)
     */
    @Transactional
    public void submitFeedback(CreateFeedbackDto request) {
        // 1. Fetch Patient and Doctor
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // 2. Create Feedback
        Feedback feedback = new Feedback();
        feedback.setPatient(patient);
        feedback.setDoctor(doctor);
        feedback.setIsAnonymous(request.getIsAnonymous());
        feedback.setCreatedAt(LocalDateTime.now());

        // 3. Set Optional Fields (These might be null now, which is allowed)
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());

        // 4. Save Feedback
        // usage of saveAndFlush ensures the DB row exists before calculation runs
        Feedback savedFeedback = feedbackRepository.saveAndFlush(feedback);

        // 5. SMART UPDATE TRIGGER
        // Only recalculate the doctor's average if the user actually provided a rating!
        // If they only left a comment (rating is null), skip this to save performance.
        if (savedFeedback.getRating() != null) {
            String doctorId = String.valueOf(savedFeedback.getDoctor().getDoctorId());
            doctorService.updateDoctorRating(doctorId);
        }
    }

    /**
     * Get all feedback for a specific doctor
     */
    @Transactional(readOnly = true)
    public ResponseEntity<List<FeedbackViewDto>> getDoctorFeedback(Long doctorId) {
        List<Feedback> feedbackList = feedbackRepository.findByDoctorDoctorIdOrderByCreatedAtDesc(doctorId);

        List<FeedbackViewDto> responseList = feedbackList.stream().map(f -> {
            FeedbackViewDto dto = new FeedbackViewDto();
            dto.setFeedbackId(f.getFeedbackId());
            dto.setRating(f.getRating()); // This can be null (comment-only feedback)
            dto.setComment(f.getComment());
            dto.setDate(f.getCreatedAt());

            // LOGIC: If anonymous, hide the name
            if (Boolean.TRUE.equals(f.getIsAnonymous())) {
                dto.setPatientName("Anonymous User");
            } else {
                dto.setPatientName(f.getPatient().getUser().getFirstName() + " " + f.getPatient().getUser().getLastName());
            }

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
}