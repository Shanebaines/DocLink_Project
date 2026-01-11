package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernel.Entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // <--- Added this import

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    // Fetch all feedback for a specific doctor, newest first
    List<Feedback> findByDoctorDoctorIdOrderByCreatedAtDesc(Long doctorId);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.doctor.doctorId = :doctorId")
    Double getAverageRating(Long doctorId);


    @Query("SELECT f.rating FROM Feedback f WHERE f.doctor.doctorId = :doctorId")
    List<Integer> findRatingsByDoctorId(@Param("doctorId") Long doctorId);
}