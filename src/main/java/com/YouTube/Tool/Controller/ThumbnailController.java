package com.YouTube.Tool.Controller;

import com.YouTube.Tool.Entity.User;
import com.YouTube.Tool.Model.ActivityType;
import com.YouTube.Tool.Model.ThumbnailData;
import com.YouTube.Tool.Repository.UserRepository;
import com.YouTube.Tool.Service.ActivityService;
import com.YouTube.Tool.Service.ThumbnailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
@RequiredArgsConstructor // Use this for clean injection
public class ThumbnailController {

    private final ThumbnailService thumbnailService;
    private final ActivityService activityService; // ADDED for logging
    private final UserRepository userRepository;   // ADDED for finding user

    @GetMapping("/thumbnails")
    public String getThumbnailPage() {
        return "thumbnails";
    }

    @PostMapping("/get-thumbnail-from-url")
    public String analyzeFromUrl(@RequestParam("thumbnailUrl") String videoUrl, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        String videoId = thumbnailService.extractVideoId(videoUrl);
        if (videoId == null) {
            model.addAttribute("error", "Invalid YouTube URL provided.");
            return "thumbnails";
        }

        String finalThumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
        ThumbnailData analysisResult = getDummyAnalysis(finalThumbnailUrl);
        model.addAttribute("thumbnailData", analysisResult);

        // --- DASHBOARD LOGIC ---
        if (userDetails != null) {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            activityService.logActivity(user, ActivityType.THUMBNAIL_ANALYSIS, videoUrl);
        }
        // --- END DASHBOARD LOGIC ---

        model.addAttribute("activeTab", "youtube");
        return "thumbnails";
    }

    @PostMapping("/get-thumbnail-from-upload")
    public String analyzeFromUpload(@RequestParam("customThumbnail") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a file to upload.");
            model.addAttribute("activeTab", "custom");
            return "thumbnails";
        }

        try {
            String imageBase64 = Base64.getEncoder().encodeToString(file.getBytes());
            String imageUrl = "data:" + file.getContentType() + ";base64," + imageBase64;
            ThumbnailData analysisResult = getDummyAnalysis(imageUrl);
            model.addAttribute("thumbnailData", analysisResult);

            // --- DASHBOARD LOGIC ---
            if (userDetails != null) {
                User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
                activityService.logActivity(user, ActivityType.THUMBNAIL_ANALYSIS, file.getOriginalFilename());
            }
            // --- END DASHBOARD LOGIC ---

        } catch (IOException e) {
            model.addAttribute("error", "Failed to process the uploaded file.");
        }

        model.addAttribute("activeTab", "custom");
        return "thumbnails";
    }

    private ThumbnailData getDummyAnalysis(String imageUrl) {
        return ThumbnailData.builder()
                .url(imageUrl)
                .score(78)
                .hasText(true)
                .hasFace(false)
                .highContrast(true)
                .recommendations(List.of(
                        "Consider adding a human face to increase click-through rate.",
                        "Colors have great contrast and stand out well.",
                        "The text is large and easy to read on small screens."
                ))
                .build();
    }
}