package com.ecommerce.analytics_backend.controller;

import com.ecommerce.analytics_backend.dto.AssistantChatRequest;
import com.ecommerce.analytics_backend.dto.AssistantChatResponse;
import com.ecommerce.analytics_backend.service.AssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService assistantService;

    @PostMapping("/chat")
    public ResponseEntity<AssistantChatResponse> chat(@RequestBody AssistantChatRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        if (request == null || request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(assistantService.chat(request, principal.getName()));
    }
}
