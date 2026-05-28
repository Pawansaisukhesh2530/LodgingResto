package com.example.lodgingresto.controller;

import com.example.lodgingresto.dto.AiRequestDto;
import com.example.lodgingresto.dto.AiResponseDto;
import com.example.lodgingresto.service.GroqService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping
public class AiAssistantController {

    private final GroqService groqService;

    public AiAssistantController(GroqService groqService) {
        this.groqService = groqService;
    }

    @GetMapping("/ai-assistant")
    public String assistantPage(Model model) {
        return "ai-assistant";
    }

    @PostMapping("/api/ai/chat")
    @ResponseBody
    public AiResponseDto chat(@Valid @RequestBody AiRequestDto request, Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : "anonymous";
        String responseText = groqService.chat(request.getMessage(), username);
        
        // Detect category dynamically for styling different cards
        String detectedCategory = "GENERAL";
        String msg = request.getMessage().toLowerCase();
        if (msg.contains("room") || msg.contains("stay") || msg.contains("suite") || msg.contains("romantic") || msg.contains("luxury")) {
            detectedCategory = "ROOM";
        } else if (msg.contains("food") || msg.contains("menu") || msg.contains("dinner") || msg.contains("veg") || msg.contains("spicy")) {
            detectedCategory = "FOOD";
        } else if (msg.contains("book") || msg.contains("reservation")) {
            detectedCategory = "BOOKING";
        }

        return new AiResponseDto(responseText, detectedCategory);
    }
}
