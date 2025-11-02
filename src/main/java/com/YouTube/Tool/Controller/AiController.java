package com.YouTube.Tool.Controller;

import com.YouTube.Tool.Entity.User;
import com.YouTube.Tool.Model.ActivityType;
import com.YouTube.Tool.Repository.UserRepository;
import com.YouTube.Tool.Service.ActivityService;
import com.YouTube.Tool.Service.AiService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ai-assistant")
@RequiredArgsConstructor
public class AiController {

    private static final Logger log = LoggerFactory.getLogger(AiController.class);

    private final AiService aiService;
    private final ActivityService activityService;
    private final UserRepository userRepository;

    @GetMapping
    public String showAiAssistantPage(Model model) {
        model.addAttribute("userPrompt", "");
        model.addAttribute("generatedContent", "");
        model.addAttribute("error", "");
        return "ai-assistant";
    }

    @PostMapping("/generate")
    public String generateContent(@RequestParam("userPrompt") String userPrompt,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {

        if (userPrompt == null || userPrompt.isBlank()) {
            model.addAttribute("error", "⚠️ Please enter a prompt before generating.");
            model.addAttribute("userPrompt", "");
            model.addAttribute("generatedContent", "");
            return "ai-assistant";
        }

        String generatedContent;
        try {
            log.info("💬 User '{}' asked Gemini: {}",
                    userDetails != null ? userDetails.getUsername() : "Anonymous",
                    userPrompt);

            generatedContent = aiService.chatWithGemini(userPrompt);

            if (generatedContent == null || generatedContent.isBlank()) {
                model.addAttribute("error", "⚠️ Gemini returned an empty response.");
                generatedContent = "";
            } else {
                log.info("✅ Gemini responded successfully.");
                logAiActivity(userDetails, userPrompt);
            }

        } catch (Exception e) {
            log.error("❌ Error while generating AI content", e);
            model.addAttribute("error", "Error generating AI content: " + e.getMessage());
            generatedContent = "";
        }

        model.addAttribute("userPrompt", userPrompt);
        model.addAttribute("generatedContent", generatedContent);

        return "ai-assistant";
    }

    private void logAiActivity(UserDetails userDetails, String details) {
        if (userDetails != null && details != null && !details.isBlank()) {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
            activityService.logActivity(user, ActivityType.AI_ASSISTANT_USE, details);
        }
    }
}
