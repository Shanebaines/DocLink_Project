package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Kernel.Service.FeedbackService;
import com.springbootpractice.doclink.Listner.Dto.Request.CreateFeedbackDto;
import com.springbootpractice.doclink.Listner.Dto.Response.FeedbackViewDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    // 1. Post Feedback
    @PostMapping("/add")
    public ResponseEntity<String> addFeedback(@RequestBody @Valid CreateFeedbackDto request) {
        feedbackService.submitFeedback(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Feedback submitted successfully");
    }

    // 2. View Feedback for a Doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<FeedbackViewDto>> getFeedbackForDoctor(@PathVariable Long doctorId) {
        return feedbackService.getDoctorFeedback(doctorId);
    }
}