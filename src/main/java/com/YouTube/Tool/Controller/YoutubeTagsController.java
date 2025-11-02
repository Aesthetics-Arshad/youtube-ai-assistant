package com.YouTube.Tool.Controller;

import com.YouTube.Tool.Entity.User;
import com.YouTube.Tool.Model.ActivityType;
import com.YouTube.Tool.Model.SearchVideo;
import com.YouTube.Tool.Model.Video;
import com.YouTube.Tool.Repository.UserRepository;
import com.YouTube.Tool.Service.ActivityService;
import com.YouTube.Tool.Service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/youtube")
@RequiredArgsConstructor // Use this for cleaner constructor injection
public class YoutubeTagsController {

    // Injected services
    private final YoutubeService youtubeService;
    private final ActivityService activityService;
    private final UserRepository userRepository;

    @Value("${youtube.api.key}")
    public String apikey;

    private boolean isConfigured() {
        return apikey != null && !apikey.isEmpty();
    }

    @PostMapping("/search")
    public String videoTags(@RequestParam("videoTitle") String videoTitle, @AuthenticationPrincipal UserDetails userDetails, Model model) {

        if (!isConfigured()) {
            model.addAttribute("error", "API key is not configured");
            return "home";
        }
        if (videoTitle == null || videoTitle.trim().isEmpty()) {
            model.addAttribute("error", "Video Title is Required");
            return "home";
        }

        try {
            // 1. Fetch search results from service
            SearchVideo result = youtubeService.searchVideos(videoTitle);

            // Filter out related videos that have no tags
            List<Video> relatedVideosWithTags = new ArrayList<>();
            if (result.getRelatedVideos() != null) {
                relatedVideosWithTags = result.getRelatedVideos().stream()
                        .filter(video -> video.getTags() != null && !video.getTags().isEmpty())
                        .collect(Collectors.toList());
            }

            model.addAttribute("primaryVideo", result.getPrimaryVideo());
            model.addAttribute("relatedVideos", relatedVideosWithTags);

            // 2. Collect all tags into a single list
            List<String> allCollectedTags = new ArrayList<>();
            if (result.getPrimaryVideo() != null && result.getPrimaryVideo().getTags() != null) {
                allCollectedTags.addAll(result.getPrimaryVideo().getTags());
            }
            for (Video video : relatedVideosWithTags) {
                allCollectedTags.addAll(video.getTags());
            }

            // 3. Add all necessary data to the model for the frontend
            model.addAttribute("tags", allCollectedTags);
            model.addAttribute("tagsCount", allCollectedTags.size());

            // --- DASHBOARD LOGIC ---
            // 4. If a user is logged in, log this activity
            if (userDetails != null) {
                User user = userRepository.findByUsername(userDetails.getUsername())
                        .orElseThrow(() -> new IllegalStateException("Cannot find logged in user"));
                activityService.logActivity(user, ActivityType.TAG_GENERATION, videoTitle);
            }
            // --- END DASHBOARD LOGIC ---

            return "home";

        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while fetching tags: " + e.getMessage());
            e.printStackTrace(); // Good for debugging
            return "home";
        }
    }
}