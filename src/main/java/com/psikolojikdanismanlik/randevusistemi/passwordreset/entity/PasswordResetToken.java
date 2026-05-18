package com.psikolojikdanismanlik.randevusistemi.passwordreset.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String otpCode;

    private LocalDateTime expirationTime;

    private boolean used;

    public PasswordResetToken() {
    }

    public PasswordResetToken(String email,String otpCode,LocalDateTime expirationTime,boolean used) {

        this.email = email;
        this.otpCode = otpCode;
        this.expirationTime = expirationTime;
        this.used = used;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public boolean isUsed() {
        return used;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}