package com.psikolojikdanismanlik.randevusistemi.dto.response;

import com.psikolojikdanismanlik.randevusistemi.enums.EmailStatus;

import java.time.LocalDateTime;
import java.util.List;

public class EmailResponseDto {

    private Long id;
    private String recipient;
    private String subject;
    private String templateName;
    private EmailStatus status;
    private String failureReason;
    private int retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private List<String> attachmentNames;

    public EmailResponseDto() {
    }

    public EmailResponseDto(Long id, String recipient, String subject, String templateName, EmailStatus status, String failureReason, int retryCount, LocalDateTime createdAt, LocalDateTime sentAt, List<String> attachmentNames) {
        this.id = id;
        this.recipient = recipient;
        this.subject = subject;
        this.templateName = templateName;
        this.status = status;
        this.failureReason = failureReason;
        this.retryCount = retryCount;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.attachmentNames = attachmentNames;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public EmailStatus getStatus() {
        return status;
    }

    public void setStatus(EmailStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public List<String> getAttachmentNames() {
        return attachmentNames;
    }

    public void setAttachmentNames(List<String> attachmentNames) {
        this.attachmentNames = attachmentNames;
    }
}
