package com.psikolojikdanismanlik.randevusistemi.passwordreset.service;

import com.psikolojikdanismanlik.randevusistemi.dto.request.EmailRequest;
import com.psikolojikdanismanlik.randevusistemi.service.EmailService;
import com.psikolojikdanismanlik.randevusistemi.passwordreset.entity.PasswordResetToken;
import com.psikolojikdanismanlik.randevusistemi.passwordreset.repository.PasswordResetTokenRepository;
import com.psikolojikdanismanlik.randevusistemi.entity.User;
import com.psikolojikdanismanlik.randevusistemi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,EmailService emailService,UserRepository userRepository,PasswordEncoder passwordEncoder
    ) {
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void sendOtp(String email) {

        String otpCode = generateOtp();
        PasswordResetToken token = new PasswordResetToken();

        token.setEmail(email);
        token.setOtpCode(otpCode);
        token.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        token.setUsed(false);

        tokenRepository.save(token);

        EmailRequest request =new EmailRequest(email,"Password Reset OTP","otp-email",Map.of("otpCode", otpCode));

        emailService.sendHtmlEmail(request);
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private boolean verifyOtp(String email, String otpCode) {
        var token = tokenRepository.findByEmailAndOtpCode(email, otpCode);

        if (token.isEmpty()) {
            return false;}

        PasswordResetToken resetToken = token.get();

        // OTP'nin süresi dolup dolmadığını kontrol et
        if (LocalDateTime.now().isAfter(resetToken.getExpirationTime())) {
            return false;}

        // OTP'nin daha önce kullanılıp kullanılmadığını kontrol et
        if (resetToken.isUsed()) {
            return false;}

        // OTP'yi kullanıldı olarak işaretle
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return true;
    }

    public void resetPassword(String email, String otpCode, String newPassword) {
        boolean isValid = verifyOtp(email, otpCode);

        if (!isValid) { throw new RuntimeException("Geçersiz veya süresi dolmuş OTP.");}

        var user = userRepository.findByEmail(email);

        if (user.isEmpty()) { throw new RuntimeException("Kullanıcı bulunamadı.");}

        User resetUser = user.get();
        resetUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(resetUser);
    }
}
