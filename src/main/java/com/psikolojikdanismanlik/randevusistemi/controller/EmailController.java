package com.psikolojikdanismanlik.randevusistemi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psikolojikdanismanlik.randevusistemi.dto.request.EmailRequest;
import com.psikolojikdanismanlik.randevusistemi.dto.response.ApiResponse;
import com.psikolojikdanismanlik.randevusistemi.dto.response.EmailResponseDto;
import com.psikolojikdanismanlik.randevusistemi.service.EmailService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public EmailController(EmailService emailService, ObjectMapper objectMapper, Validator validator) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @PostMapping("/send")
    public ApiResponse<EmailResponseDto> sendEmail(@RequestBody @Valid EmailRequest request) {
        EmailResponseDto response = emailService.sendHtmlEmail(request, null);
        return ApiResponse.success("Email queued for delivery.", response);
    }

    @PostMapping(value = "/send-with-attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<EmailResponseDto> sendEmailWithAttachments(
            @RequestPart("email") String emailJson,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        EmailRequest request = parseAndValidateEmailRequest(emailJson);
        EmailResponseDto response = emailService.sendHtmlEmail(request, attachments);
        return ApiResponse.success("Email queued for delivery with attachments.", response);
    }

    private EmailRequest parseAndValidateEmailRequest(String emailJson) {
        try {
            EmailRequest request = objectMapper.readValue(emailJson, EmailRequest.class);
            Set<ConstraintViolation<EmailRequest>> violations = validator.validate(request);

            if (!violations.isEmpty()) {
                String message = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }

            return request;
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email part must be a valid JSON object.", e);
        }
    }
}
