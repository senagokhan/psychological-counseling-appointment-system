package com.psikolojikdanismanlik.randevusistemi.service;

import com.psikolojikdanismanlik.randevusistemi.dto.request.EmailRequest;
import com.psikolojikdanismanlik.randevusistemi.dto.response.EmailResponseDto;
import com.psikolojikdanismanlik.randevusistemi.entity.Email;
import com.psikolojikdanismanlik.randevusistemi.entity.EmailAttachment;
import com.psikolojikdanismanlik.randevusistemi.enums.EmailStatus;
import com.psikolojikdanismanlik.randevusistemi.repository.EmailRepository;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final String SENSITIVE_BODY_PLACEHOLDER = "[Sensitive email body was not stored.]";

    private final JavaMailSender javaMailSender;
    private final EmailTemplateService emailTemplateService;
    private final EmailRepository emailRepository;

    @Value("${spring.mail.username}")
    private String sender;

    public EmailService(JavaMailSender javaMailSender,EmailTemplateService emailTemplateService,EmailRepository emailRepository) {
        this.javaMailSender = javaMailSender;
        this.emailTemplateService = emailTemplateService;
        this.emailRepository = emailRepository;
    }

    public void sendHtmlEmail(EmailRequest request) {
        sendHtmlEmail(request, null);
    }

    public EmailResponseDto sendHtmlEmail(EmailRequest request, List<MultipartFile> attachments) {
        String htmlContent = emailTemplateService.generateContent(request.getTemplateName(), request.getVariables());
        List<EmailAttachment> emailAttachments = copyAttachments(attachments);
        List<String> attachmentNames = extractAttachmentNames(emailAttachments);

        Email email = new Email();
        email.setRecipient(request.getTo());
        email.setSubject(request.getSubject());
        email.setTemplateName(request.getTemplateName());
        email.setBody(resolveBodyForPersistence(request.getTemplateName(), htmlContent));
        email.setStatus(EmailStatus.PENDING);
        email.setCreatedAt(LocalDateTime.now());
        email.setAttachmentNames(attachmentNames);
        emailAttachments.forEach(email::addAttachment);

        Email savedEmail = emailRepository.save(email);
        CompletableFuture.runAsync(() -> sendHtmlEmailAsync(savedEmail, htmlContent, emailAttachments));

        return mapToDto(savedEmail);
    }

    private void sendHtmlEmailAsync(Email email, String htmlContent, List<EmailAttachment> attachments) {
        try {
            sendMimeMessage(email, htmlContent, attachments);
            markAsSent(email);
            logger.info("Email sent successfully to {}", email.getRecipient());
        } catch (Exception e) {
            email.setStatus(EmailStatus.FAILED);
            email.setRetryCount(1);
            email.setFailureReason(e.getMessage());
            emailRepository.save(email);
            logger.warn("Email delivery failed for {}. Scheduled retry will pick it up.", email.getRecipient(), e);
        }
    }

    @Scheduled(
            fixedDelayString = "${app.mail.retry.fixed-delay-ms:30000}",
            initialDelayString = "${app.mail.retry.initial-delay-ms:30000}"
    )
    
    public void retryFailedEmails() {
        List<Email> failedEmails = emailRepository.findTop10ByStatusAndRetryCountLessThanOrderByCreatedAtAsc(EmailStatus.FAILED, MAX_RETRY_ATTEMPTS);

        for (Email email : failedEmails) {
            retryFailedEmail(email);
        }
    }

    private void retryFailedEmail(Email email) {
        if (SENSITIVE_BODY_PLACEHOLDER.equals(email.getBody())) {
            email.setRetryCount(MAX_RETRY_ATTEMPTS);
            email.setFailureReason("Scheduled retry skipped because the sensitive email body was not stored.");
            emailRepository.save(email);
            logger.warn("Scheduled retry skipped for sensitive email {}", email.getId());
            return;
        }

        try {
            sendMimeMessage(email, email.getBody(), email.getAttachments());
            markAsSent(email);
            logger.info("Scheduled email retry succeeded for email {}", email.getId());
        } catch (Exception e) {
            email.setStatus(EmailStatus.FAILED);
            email.setRetryCount(email.getRetryCount() + 1);
            email.setFailureReason(e.getMessage());
            emailRepository.save(email);
            logger.warn("Scheduled email retry failed for email {}. Retry count: {}", email.getId(), email.getRetryCount(), e);
        }
    }

    private void sendMimeMessage(Email email, String htmlContent, List<EmailAttachment> attachments) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(sender);
        helper.setTo(email.getRecipient());
        helper.setSubject(email.getSubject());
        helper.setText(htmlContent, true);

        for (EmailAttachment attachment : attachments) {
            ByteArrayResource resource = new ByteArrayResource(attachment.getContent());
            helper.addAttachment(attachment.getFilename(), resource);
        }

        javaMailSender.send(mimeMessage);
    }

    private void markAsSent(Email email) {
        email.setStatus(EmailStatus.SENT);
        email.setSentAt(LocalDateTime.now());
        email.setFailureReason(null);
        emailRepository.save(email);
    }

    private List<EmailAttachment> copyAttachments(List<MultipartFile> attachments) {
        if (attachments == null) {
            return Collections.emptyList();
        }

        List<EmailAttachment> copiedAttachments = new ArrayList<>();

        for (MultipartFile file : attachments) {
            if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
                continue;
            }

            try {
                EmailAttachment attachment = new EmailAttachment();
                attachment.setFilename(file.getOriginalFilename());
                attachment.setContentType(file.getContentType());
                attachment.setContent(file.getBytes());
                copiedAttachments.add(attachment);
            } catch (IOException e) {
                throw new IllegalArgumentException("Attachment could not be read: " + file.getOriginalFilename(), e);
            }
        }

        return copiedAttachments;
    }

    private List<String> extractAttachmentNames(List<EmailAttachment> attachments) {
        return attachments.stream()
                .map(EmailAttachment::getFilename)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String resolveBodyForPersistence(String templateName, String htmlContent) {
        if (isSensitiveTemplate(templateName)) {
            return SENSITIVE_BODY_PLACEHOLDER;
        }

        return htmlContent;
    }

    private boolean isSensitiveTemplate(String templateName) {
        if (templateName == null) {
            return false;
        }

        String normalizedTemplateName = templateName.toLowerCase(Locale.ROOT);
        return normalizedTemplateName.contains("otp") || normalizedTemplateName.contains("password");
    }

    private EmailResponseDto mapToDto(Email email) {
        return new EmailResponseDto(
                email.getId(),
                email.getRecipient(),
                email.getSubject(),
                email.getTemplateName(),
                email.getStatus(),
                email.getFailureReason(),
                email.getRetryCount(),
                email.getCreatedAt(),
                email.getSentAt(),
                email.getAttachmentNames()
        );
    }
}
