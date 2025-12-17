package com.springbootpractice.doclink.Kernel.Relations;

import com.springbootpractice.doclink.Kernel.Entity.Prescription;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescription_tokens")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PrescriptionToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String token;                      // e.g. "RX7F-82KD-Z9"

    @OneToOne
    @JoinColumn(name = "prescription_id", nullable = false, unique = true)
    private Prescription prescription;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean active;

    @PrePersist
    protected void onCreate() {
        if (expiresAt == null)
            expiresAt = LocalDateTime.now().plusDays(1);
        if (active == null) active = true;
    }
}