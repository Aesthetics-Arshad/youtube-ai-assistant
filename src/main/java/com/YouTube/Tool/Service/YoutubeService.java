package com.YouTube.Tool.Service;


import com.YouTube.Tool.Model.SearchVideo;
import com.YouTube.Tool.Model.Video;
import com.YouTube.Tool.Model.VideoDetails;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YoutubeService {


    private final WebClient.Builder webClientBuilder;
    @Value("${youtube.api.key}")
    private String apiKey;


    @Value("${youtube.api.base.url}")
    private String baseUrl;

    @Value("${youtube.api.max.related.videos}")
    private int maxRelatedVideos;

    public SearchVideo searchVideos(String videoTitle) {

        List<String> videoIds = searchForVideoIds(videoTitle);


        if (videoIds.isEmpty()) {
            return SearchVideo.builder()
                    .primaryVideo(null)
                    .relatedVideos(Collections.emptyList())
                    .build();
        }


        String primaryVideoId = videoIds.get(0);
        List<String> relatedVideoIds = videoIds.subList(1, Math.min(videoIds.size(), maxRelatedVideos));

        Video primaryVideo = getVideoById(primaryVideoId);
        List<Video> relatedVideos = new ArrayList<>();
        for (String id : relatedVideoIds) {
            Video video = getVideoById(id);
            if (video != null) {
                relatedVideos.add(video);
            }
        }
        return SearchVideo.builder()
                .primaryVideo(primaryVideo)
                .relatedVideos(relatedVideos)
                .build();

    }

    // YoutubeService.java mein is method ko replace karo

    private List<String> searchForVideoIds(String videoTitle) {

        // JAASOOSI LINE 1: Dekho ki API call sahi URL se jaa rahi hai ya nahi
        System.out.println("--- Calling YouTube API for title: " + videoTitle);

        SearchApiResponse response = webClientBuilder.baseUrl(baseUrl).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("part", "snippet")
                        .queryParam("q", videoTitle)
                        .queryParam("type", "video")
                        .queryParam("maxResults", maxRelatedVideos)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(SearchApiResponse.class)
                .block();

        if (response == null || response.items == null) {
            // JAASOOSI LINE 2: Agar response null hai to pata chal jayega
            System.out.println("--- API Response was NULL or had no items!");
            return Collections.emptyList();
        }

        // JAASOOSI LINE 3: Dekho ki API se kitni video IDs mili
        System.out.println("--- Found " + response.items.size() + " video IDs from API.");

        List<String> videoIds = new ArrayList<>();
        for (SearchItem item : response.items) {
            videoIds.add(item.id.videoId);
        }

        return videoIds;
    }

    public VideoDetails getVideoDetails(String videoId){
        VideoApiResponse response=webClientBuilder.baseUrl(baseUrl).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part","snippet")
                        .queryParam("id",videoId)
                        .queryParam("key",apiKey)
                        .build())
                .retrieve()
                .bodyToMono(VideoApiResponse.class)
                .block();

        if (response==null || response.items==null){
            return null;
        }
        Snippet snippet=response.items.get(0).snippet;
        String thumbnailUrl =snippet.thumbnails.getBestThumbnailsUrl();
        return VideoDetails.builder()
                .id(videoId)
                .title(snippet.title)
                .description(snippet.description)
                .tags(snippet.tags==null ? Collections.emptyList():snippet.tags)
                .thumbnailUrl(thumbnailUrl)
                .channelTitle(snippet.channelTitle)
                .publishedAt(snippet.publishedAt)
                .build();



    }

    private Video getVideoById(String videoId){
        VideoApiResponse response=webClientBuilder.baseUrl(baseUrl).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part","snippet")
                        .queryParam("id",videoId)
                        .queryParam("key",apiKey)
                        .build())
                .retrieve()
                .bodyToMono(VideoApiResponse.class)
                .block();

        if (response==null || response.items==null){
            return null;
        }
        Snippet snippet=response.items.get(0).snippet;
             return Video.builder()
                .id(videoId)
                .channelTitle(snippet.channelTitle)
                .title(snippet.title)
                .tags(snippet.tags == null? Collections.emptyList():snippet.tags)
                .build();

    }

    @Data
    static class SearchApiResponse{
        List<SearchItem>items;
    }
    @Data
    static class SearchItem{
        Id id;
    }
    @Data
    static class Id{
        String videoId;
    }
    @Data
            static class VideoApiResponse{
        List<VideoItem> items;
    }
    @Data

            static class VideoItem{
        Snippet snippet;
    }
    @Data
            static class Snippet{
        String title;
        String description;
        String channelTitle;
        String publishedAt;
        List<String> tags;
        Thumbnails thumbnails;

    }
    @Data
    static class ThumbnailsDetails{

        String url;
        int width;
        int height;
    }
    @Data
            static class Thumbnails{
        ThumbnailsDetails maxres;
        ThumbnailsDetails high;
        ThumbnailsDetails medium;
        ThumbnailsDetails _default;

        String getBestThumbnailsUrl(){
            if(maxres!=null)return maxres.url;
            if(high!=null)return high.url;
            if (medium!=null)return medium.url;
            return _default !=null? _default.url: "";
        }

    }






}
