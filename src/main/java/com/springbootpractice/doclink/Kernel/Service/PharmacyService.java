package com.springbootpractice.doclink.Kernel.Service;

import com.springbootpractice.doclink.Kernel.Entity.Prescription;
import com.springbootpractice.doclink.Kernel.Entity.PrescriptionMedication;
import com.springbootpractice.doclink.Kernel.Relations.PrescriptionToken;
import com.springbootpractice.doclink.Dealer.PrescriptionTokenRepository;
import com.springbootpractice.doclink.Listner.Dto.Response.PrescriptionMedicationResponse_Pharmacy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PharmacyService {

    private final PrescriptionTokenRepository tokenRepository;

    /**
     * Resolves token, validates expiry, and returns prescribed medication details.
     */
    public List<PrescriptionMedicationResponse_Pharmacy> getMedicationsByToken(String tokenValue) {
        log.info("Pharmacy requested medications for token: {}", tokenValue);

        // Validate token
        PrescriptionToken token = tokenRepository.findByTokenAndActiveTrue(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or inactive token."));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Token {} has expired at {}", tokenValue, token.getExpiresAt());
            token.setActive(false); // Mark inactive
            tokenRepository.save(token);
            throw new IllegalArgumentException("Token expired.");
        }

        Prescription prescription = token.getPrescription();
        List<PrescriptionMedication> meds = prescription.getPrescriptionMedications();

        log.info("Found {} medications for prescription ID {}", meds.size(), prescription.getPrescriptionId());

        return meds.stream()
                .map(pm -> {
                    PrescriptionMedicationResponse_Pharmacy dto = new PrescriptionMedicationResponse_Pharmacy();
                    dto.setMedicationName(pm.getMedication().getMedicationName());
                    dto.setDosage(pm.getDosage());
                    dto.setFrequency(pm.getFrequency());
                    dto.setDurationDays(pm.getDurationDays());
                    dto.setQuantity(pm.getQuantity());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}