package com.YouTube.Tool.Controller;

import com.YouTube.Tool.Entity.User;
import com.YouTube.Tool.Model.ActivityType;
import com.YouTube.Tool.Model.VideoDetails;
import com.YouTube.Tool.Repository.UserRepository;
import com.YouTube.Tool.Service.ActivityService;
import com.YouTube.Tool.Service.ThumbnailService;
import com.YouTube.Tool.Service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/youtube")
@RequiredArgsConstructor
public class YoutubeVideoController {

    private final YoutubeService youtubeService;
    private final ThumbnailService service; // Renamed for clarity
    private final ActivityService activityService; // ADDED for logging
    private final UserRepository userRepository;   // ADDED for finding user

    @Value("${youtube.api.key}")
    public String apikey;

    @GetMapping("/video-details")
    public String showVideoForm(){
        return "video-details";
    }

    private boolean isConfigured(){
        return apikey!=null && !apikey.isEmpty();
    }

    @PostMapping("/video-details")
    public String fetchVideoDetails(@RequestParam("videoUrl") String videoUrl, @AuthenticationPrincipal UserDetails userDetails, Model model){
        if(!isConfigured()){
            model.addAttribute("error","API key is not configured.");
            return "video-details";
        }
        String videoId=service.extractVideoId(videoUrl);
        if(videoId==null){
            model.addAttribute("error","Invalid Youtube Url or ID");
            return "video-details";
        }

        VideoDetails details=youtubeService.getVideoDetails(videoId);
        if(details==null){
            model.addAttribute("error","Video Not Found");
        } else {
            model.addAttribute("videoDetails",details);
            // --- DASHBOARD LOGIC ---
            if (userDetails != null) {
                User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
                activityService.logActivity(user, ActivityType.VIDEO_DETAILS_EXTRACTION, videoUrl);
            }
            // --- END DASHBOARD LOGIC ---
        }

        model.addAttribute("videoUrlOrId", videoUrl); // Keeps the URL in the input box for reference
        return "video-details";
    }
}