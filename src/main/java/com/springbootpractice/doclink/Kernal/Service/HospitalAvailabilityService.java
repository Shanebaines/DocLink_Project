package com.springbootpractice.doclink.Kernal.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springbootpractice.doclink.Dealer.DoctorAvailabilityRepository;
import com.springbootpractice.doclink.Dealer.DoctorRepository;
import com.springbootpractice.doclink.Dealer.DoctorTimeSlotRepository;
import com.springbootpractice.doclink.Dealer.HospitalRepository;
import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import com.springbootpractice.doclink.Kernal.Entity.Hospital;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_availability;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_time_slots;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class HospitalAvailabilityService {
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final DoctorTimeSlotRepository doctorTimeSlotRepository;
    private final EntityManager em;

    @Transactional
    public Doctor_availability updateAvailability(Long doctorId, Long hospitalId, LocalDate date, String status) {
        boolean coming = "coming".equalsIgnoreCase(status);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("doctorId not found: " + doctorId));
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("hospitalId not found: " + hospitalId));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // Find existing availability that covers this date (overlap)
        String jpql = "SELECT da FROM Doctor_availability da WHERE da.doctor.doctorId = :doctorId AND da.hospital.hospitalId = :hospitalId " +
                "AND da.effectiveFrom <= :endOfDay AND da.effectiveUntil >= :startOfDay";
        TypedQuery<Doctor_availability> q = em.createQuery(jpql, Doctor_availability.class)
                .setParameter("doctorId", doctorId)
                .setParameter("hospitalId", hospitalId)
                .setParameter("startOfDay", startOfDay)
                .setParameter("endOfDay", endOfDay)
                .setMaxResults(1);
        List<Doctor_availability> matches = q.getResultList();

        if (!matches.isEmpty()) {
            Doctor_availability existing = matches.get(0);
            existing.setAvailability(coming);
            // leave effectiveFrom/effectiveUntil as-is
            return doctorAvailabilityRepository.save(existing);
        }

        // not found -> create a slot that covers the whole day and create availability
        Doctor_time_slots slot = Doctor_time_slots.builder()
                .doctor(doctor)
                .hospital(hospital)
                .dayOfWeek(date.getDayOfWeek())
                .startTime(LocalTime.MIDNIGHT)
                .endTime(LocalTime.of(23,59))
                .totalSeats(1)
                .build();
        slot = doctorTimeSlotRepository.save(slot);

        Doctor_availability da = Doctor_availability.builder()
                .doctor(doctor)
                .hospital(hospital)
                .slot(slot)
                .availability(coming)
                .effectiveFrom(startOfDay)
                .effectiveUntil(endOfDay)
                .build();

        return doctorAvailabilityRepository.save(da);
    }
}
