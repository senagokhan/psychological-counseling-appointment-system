package com.psikolojikdanismanlik.randevusistemi.passwordreset.controller;

import com.psikolojikdanismanlik.randevusistemi.passwordreset.dto.ForgotPasswordRequest;
import com.psikolojikdanismanlik.randevusistemi.passwordreset.dto.ResetPasswordRequest;
import com.psikolojikdanismanlik.randevusistemi.passwordreset.service.PasswordResetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        passwordResetService.sendOtp(request.getEmail());
        return ResponseEntity.ok("OTP mail gönderildi.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(
                    request.getEmail(),
                    request.getOtpCode(),
                    request.getNewPassword()
            );
            return ResponseEntity.ok("Şifre başarıyla sıfırlandı.");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Şifre sıfırlama başarısız: " + e.getMessage());
        }
    }
}