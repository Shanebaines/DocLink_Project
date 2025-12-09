package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Kernal.Service.PharmacyService;
import com.springbootpractice.doclink.Listner.Dto.Response.PrescriptionMedicationResponse_Pharmacy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pharmacy")
@RequiredArgsConstructor
@Slf4j
public class PharmacyController {

    private final PharmacyService pharmacyService;

    /**
     * Endpoint for pharmacy to fetch medications using patient's token.
     * Example call: GET /pharmacy/medications/{token}
     */
    @GetMapping("/medications/{token}")
    public ResponseEntity<List<PrescriptionMedicationResponse_Pharmacy>> getMedications(
            @PathVariable String token) {
        try {
            List<PrescriptionMedicationResponse_Pharmacy> meds =
                    pharmacyService.getMedicationsByToken(token);
            return ResponseEntity.ok(meds);
        } catch (IllegalArgumentException ex) {
            log.warn("Token validation failed: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception ex) {
            log.error("Unexpected error retrieving medications", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}