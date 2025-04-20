package com.vuog.telebotmanager.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageTemplateService {
    public String renderTemplate(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}


// example:
//String msg = templateService.renderTemplate(
//        "Xin chào {{name}}, đơn hàng {{orderId}} của bạn đã được xác nhận.",
//        Map.of("name", "Vuong", "orderId", "A1234")
//);