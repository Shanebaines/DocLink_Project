package com.springbootpractice.doclink.Dealer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springbootpractice.doclink.Kernal.Relations.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByHospitalIdOrderByCreatedAtDesc(Long hospitalId);
}
