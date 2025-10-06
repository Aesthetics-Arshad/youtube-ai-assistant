package com.YouTube.Tool.Controller;

import com.YouTube.Tool.Service.AiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ai-assistant") // Base path set kiya
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping
    public String showAiAssistant(Model model) {
        return "ai-assistant";
    }

    @PostMapping("/generate-all") // Naya, unified endpoint
    public String generateAll(@RequestParam("videoIdea") String videoIdea, Model model) {
        if (videoIdea == null || videoIdea.trim().isEmpty()) {
            model.addAttribute("error", "Please provide a video idea.");
            return "ai-assistant";
        }

        try {
            // Ek hi baar mein teeno cheezein generate karo
            String generatedTitles = aiService.generateTitles(videoIdea);
            String generatedDescription = aiService.generateDescription(videoIdea);
            String generatedTags = aiService.generateTags(videoIdea);

            model.addAttribute("videoIdea", videoIdea);
            model.addAttribute("generatedTitles", generatedTitles);
            model.addAttribute("generatedDescription", generatedDescription);
            model.addAttribute("generatedTags", generatedTags);

        } catch (Exception e) {
            model.addAttribute("error", "Error generating AI content: " + e.getMessage());
            e.printStackTrace();
        }
        return "ai-assistant";
    }
}