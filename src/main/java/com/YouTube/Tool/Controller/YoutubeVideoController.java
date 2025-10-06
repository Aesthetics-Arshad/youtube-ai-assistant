package com.YouTube.Tool.Controller;


import com.YouTube.Tool.Model.VideoDetails;
import com.YouTube.Tool.Service.ThumbnailService;
import com.YouTube.Tool.Service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/youtube")
public class YoutubeVideoController {

    private final YoutubeService youtubeService;
    private final ThumbnailService service;
    @GetMapping("/video-details")
    public String showVideoForm(){
        return "video-details";
    }

    @Value("${youtube.api.key}")
    public String apikey;

    private boolean isConfigured(){ // Helper method
        return apikey!=null && !apikey.isEmpty();
    }
    @PostMapping("/video-details")
        public String fetchVideoDetails(@RequestParam String videoUrlOrId, Model model){
        if(!isConfigured()){ // <--- Add this check
            model.addAttribute("error","API key is not configured.");
            return "video-details";
        }
            String videoId=service.extractVideoId(videoUrlOrId);
            if(videoId==null){
                model.addAttribute("error","Invalid Youtube Url or ID");
                return "video-details";

            }
            VideoDetails details=youtubeService.getVideoDetails(videoId);
            if(details==null){
                model.addAttribute("error","video Not Found");
            }
            else{
                model.addAttribute("videoDetails",details);
            }
            model.addAttribute("videoUrlOrId",videoUrlOrId);
            return "video-details";
    }
}
