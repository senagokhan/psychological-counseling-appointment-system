package com.psikolojikdanismanlik.randevusistemi.service;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailTemplateService {

    private final TemplateEngine templateEngine;

    public EmailTemplateService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String generateContent(String templateName, Map<String, Object> variables) {
        Context context = new Context();

        if (variables != null) {
            context.setVariables(variables);
        }

        return templateEngine.process("email/" + templateName, context);
    }
}