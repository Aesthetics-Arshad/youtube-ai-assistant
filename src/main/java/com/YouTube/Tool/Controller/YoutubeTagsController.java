package com.YouTube.Tool.Controller;


import com.YouTube.Tool.Model.SearchVideo;
import com.YouTube.Tool.Model.Video;
import com.YouTube.Tool.Service.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/youtube")
public class YoutubeTagsController {

    @Autowired
    private YoutubeService youtubeService;


    @Value("${youtube.api.key}")
    public String apikey;


    private boolean isConfigured(){
        return apikey!=null && !apikey.isEmpty();

    }


    // YoutubeTagsController.java

    @PostMapping("/search")
    public String videoTags(@RequestParam("videoTitle") String videoTitle, Model model) {

        if (!isConfigured()) {
            model.addAttribute("error", "Api key is not configured");
            return "home";
        }
        if (videoTitle == null || videoTitle.isEmpty()) {
            model.addAttribute("error", "Video Title is Required");
            return "home";
        }
        try {
            // 1. Service se search result fetch kiya
            SearchVideo result = youtubeService.searchVideos(videoTitle);
            model.addAttribute("primaryVideo", result.getPrimaryVideo());
            model.addAttribute("relatedVideos", result.getRelatedVideos());

            // --- YEH NEW LOGIC HAI JO MISSING THA ---
            // 2. Ek khali list banayi saare tags ko collect karne ke liye
            List<String> allCollectedTags = new ArrayList<>();

            // 3. Primary video ke tags add kiye (null check ke saath)
            if (result.getPrimaryVideo() != null && result.getPrimaryVideo().getTags() != null) {
                allCollectedTags.addAll(result.getPrimaryVideo().getTags());
            }

            // 4. Related videos ke tags add kiye (null check ke saath)
            if (result.getRelatedVideos() != null) {
                for (Video video : result.getRelatedVideos()) {
                    if (video.getTags() != null) {
                        allCollectedTags.addAll(video.getTags());
                    }
                }
            }

            // 5. Saare collected tags se ek single, comma-separated string banayi
            String allTagsString = String.join(", ", allCollectedTags);

            // 6. Us string ko 'allTagsAsString' naam se model mein add kiya
            model.addAttribute("allTagsAsString", allTagsString);
            // --- END OF NEW LOGIC ---

            return "home";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "home";
        }
    }

}
