package com.psikolojikdanismanlik.randevusistemi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

public class EmailRequest {

    @NotBlank(message = "Recipient email is required.")
    @Email(message = "Recipient email must be valid.")
    private String to;

    @NotBlank(message = "Email subject is required.")
    @Size(max = 150, message = "Email subject cannot exceed 150 characters.")
    private String subject;

    @NotBlank(message = "Email template name is required.")
    @Size(max = 80, message = "Email template name cannot exceed 80 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Email template name can only contain letters, numbers, underscores and hyphens.")
    private String templateName;

    private Map<String, Object> variables;

    private List<String> attachmentNames;

    public EmailRequest() {
    }

    public EmailRequest(String to, String subject, String templateName, Map<String, Object> variables) {
        this.to = to;
        this.subject = subject;
        this.templateName = templateName;
        this.variables = variables;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getTemplateName() {
        return templateName;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public List<String> getAttachmentNames() {
        return attachmentNames;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public void setAttachmentNames(List<String> attachmentNames) {
        this.attachmentNames = attachmentNames;
    }
}
