package com.ecommerce.analytics_backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @PostMapping("/ask")
    public Map<String, Object> askQuestion(@RequestBody Map<String, String> request) {
        String question = request.getOrDefault("question", "");
        
        Map<String, Object> response = new HashMap<>();
        response.put("question", question);
        response.put("answer", "This is a dummy response from the AI agent endpoint. You asked: " + question);
        // We will eventually forward this request to the Python LangGraph service
        
        return response;
    }
}
