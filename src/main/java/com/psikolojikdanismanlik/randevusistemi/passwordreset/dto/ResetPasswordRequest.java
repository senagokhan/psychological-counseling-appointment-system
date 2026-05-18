package com.psikolojikdanismanlik.randevusistemi.passwordreset.dto;

public class ResetPasswordRequest {

    private String email;
    private String otpCode;
    private String newPassword;

    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(String email, String otpCode, String newPassword) {
        this.email = email;
        this.otpCode = otpCode;
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}