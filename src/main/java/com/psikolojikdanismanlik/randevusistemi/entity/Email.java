package com.psikolojikdanismanlik.randevusistemi.entity;

import com.psikolojikdanismanlik.randevusistemi.enums.EmailStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "emails")
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "template_name")
    private String templateName;

    @Lob
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EmailStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @ElementCollection
    @CollectionTable(name = "email_attachments", joinColumns = @JoinColumn(name = "email_id"))
    @Column(name = "attachment_name")
    private List<String> attachmentNames = new ArrayList<>();

    @OneToMany(mappedBy = "email", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<EmailAttachment> attachments = new ArrayList<>();

    public void addAttachment(EmailAttachment attachment) {
        attachments.add(attachment);
        attachment.setEmail(this);
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = EmailStatus.PENDING;
        }
    }
}
