package com.psikolojikdanismanlik.randevusistemi.passwordreset.dto;

public class VerifyOtpRequest {

    private String email;

    private String otpCode;

    public VerifyOtpRequest() {
    }

    public VerifyOtpRequest(String email, String otpCode) {
        this.email = email;
        this.otpCode = otpCode;
    }

    public String getEmail() {
        return email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}