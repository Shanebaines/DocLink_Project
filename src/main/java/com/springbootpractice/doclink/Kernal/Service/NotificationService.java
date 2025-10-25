package com.springbootpractice.doclink.Kernal.Service;

import org.springframework.stereotype.Service;

import com.springbootpractice.doclink.Dealer.DoctorRepository;
import com.springbootpractice.doclink.Dealer.HospitalRepository;
import com.springbootpractice.doclink.Dealer.NotificationRepository;
import com.springbootpractice.doclink.Kernal.Relations.Notification;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;

    public void sendAvailabilityNotification(Long doctorId, Long hospitalId, String message, String status) {
        // validate ids
        if (!doctorRepository.existsById(doctorId)) {
            throw new IllegalArgumentException("doctorId not found: " + doctorId);
        }
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new IllegalArgumentException("hospitalId not found: " + hospitalId);
        }

        Notification n = Notification.builder()
                .doctorId(doctorId)
        .hospitalId(hospitalId)
        .dispensaryId(hospitalId) // populate dispensary_id to satisfy existing DB constraint
                .message(message)
                .status(status)
                .type("availability")
                .build();

        notificationRepository.save(n);

        // Note: actual external push/email/SMS delivery is out of scope.
    }
}
