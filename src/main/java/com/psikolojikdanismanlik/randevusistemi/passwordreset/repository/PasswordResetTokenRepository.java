package com.psikolojikdanismanlik.randevusistemi.passwordreset.repository;

import com.psikolojikdanismanlik.randevusistemi.passwordreset.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken>
    findTopByEmailOrderByExpirationTimeDesc(String email);

    Optional<PasswordResetToken>
    findByEmailAndOtpCode(String email, String otpCode);
}