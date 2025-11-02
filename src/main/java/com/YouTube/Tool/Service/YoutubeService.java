package com.YouTube.Tool.Service;

import com.YouTube.Tool.Model.SearchVideo;
import com.YouTube.Tool.Model.SeoReport;
import com.YouTube.Tool.Model.Video;
import com.YouTube.Tool.Model.VideoDetails;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
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
        List<String> relatedVideoIds = videoIds.subList(1, Math.min(videoIds.size(), maxRelatedVideos + 1));

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

    private List<String> searchForVideoIds(String videoTitle) {
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
            return Collections.emptyList();
        }

        List<String> videoIds = new ArrayList<>();
        for (SearchItem item : response.items) {
            videoIds.add(item.id.videoId);
        }
        return videoIds;
    }


    // REPLACE your old generateSeoReport method with this one

    private SeoReport generateSeoReport(Snippet snippet) {
        List<SeoReport.ChecklistItem> checklist = new ArrayList<>();
        int titleScore = 0;
        int descriptionScore = 0;
        int tagsScore = 0;
        String title = snippet.getTitle().toLowerCase();
        String description = snippet.getDescription().toLowerCase();
        List<String> tags = snippet.getTags() != null ? snippet.getTags() : Collections.emptyList();

        // --- Title Score Calculation (out of 100) ---
        if (title.length() >= 30 && title.length() <= 70) titleScore += 50;
        checklist.add(SeoReport.ChecklistItem.builder().text("Optimal title length (30-70 chars)").passed(title.length() >= 30 && title.length() <= 70).priority("HIGH").build());

        if (tags.stream().anyMatch(title::contains)) titleScore += 30;
        checklist.add(SeoReport.ChecklistItem.builder().text("Primary keyword found in title").passed(tags.stream().anyMatch(title::contains)).priority("HIGH").build());

        if (Character.isDigit(title.charAt(0)) || title.startsWith("how to")) titleScore += 20;
        checklist.add(SeoReport.ChecklistItem.builder().text("Title uses a number or 'How To'").passed(Character.isDigit(title.charAt(0)) || title.startsWith("how to")).priority("MEDIUM").build());

        // --- Description Score Calculation (out of 100) ---
        if (description.length() >= 300) descriptionScore += 40;
        checklist.add(SeoReport.ChecklistItem.builder().text("Detailed description (>300 chars)").passed(description.length() >= 300).priority("MEDIUM").build());

        if (!tags.isEmpty() && description.contains(tags.get(0))) descriptionScore += 40;
        checklist.add(SeoReport.ChecklistItem.builder().text("Primary keyword in first 100 chars").passed(!tags.isEmpty() && description.substring(0, Math.min(100, description.length())).contains(tags.get(0))).priority("HIGH").build());

        if (description.contains("http://") || description.contains("https://")) descriptionScore += 20;
        checklist.add(SeoReport.ChecklistItem.builder().text("Includes links to external resources").passed(description.contains("http://") || description.contains("https://")).priority("LOW").build());

        // --- Tags Score Calculation (out of 100) ---
        if (tags.size() >= 8 && tags.size() <= 20) tagsScore += 50;
        checklist.add(SeoReport.ChecklistItem.builder().text("Optimal tag count (8-20 tags)").passed(tags.size() >= 8 && tags.size() <= 20).priority("HIGH").build());

        if (!tags.isEmpty() && title.contains(tags.get(0))) tagsScore += 30;
        checklist.add(SeoReport.ChecklistItem.builder().text("Primary tag matches title content").passed(!tags.isEmpty() && title.contains(tags.get(0))).priority("HIGH").build());

        long longTailTags = tags.stream().filter(tag -> tag.trim().contains(" ")).count();
        if (longTailTags >= 2) tagsScore += 20;
        checklist.add(SeoReport.ChecklistItem.builder().text("Includes long-tail keywords (2+ words)").passed(longTailTags >= 2).priority("MEDIUM").build());

        return SeoReport.builder()
                .titleScore(Math.min(100, titleScore))
                .descriptionScore(Math.min(100, descriptionScore))
                .tagsScore(Math.min(100, tagsScore))
                .checklist(checklist)
                .build();
    }






    public VideoDetails getVideoDetails(String videoId) {
        VideoApiResponse response = webClientBuilder.baseUrl(baseUrl).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part", "snippet,statistics,contentDetails") // Requesting more data
                        .queryParam("id", videoId)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(VideoApiResponse.class)
                .block();

        if (response == null || response.items == null || response.items.isEmpty()) {
            return null;
        }

        VideoItem videoItem = response.items.get(0);
        Snippet snippet = videoItem.snippet;
        Statistics stats = videoItem.statistics;
        ContentDetails content = videoItem.contentDetails;

        // Helper methods to format data for the UI
        String formattedViews = (stats != null && stats.viewCount != null) ? formatCount(Long.parseLong(stats.viewCount)) : "0";
        String formattedLikes = (stats != null && stats.likeCount != null) ? formatCount(Long.parseLong(stats.likeCount)) : "0";
        String formattedDuration = (content != null && content.duration != null) ? formatDuration(content.duration) : "00:00";
        long commentCount = (stats != null && stats.commentCount != null) ? Long.parseLong(stats.commentCount) : 0;

        return VideoDetails.builder()
                .id(videoId)
                .title(snippet.title)
                // ... (all other fields like description, tags, etc., remain the same)
                .description(snippet.description)
                .tags(snippet.tags == null ? Collections.emptyList() : snippet.tags)
                .thumbnail(snippet.thumbnails.getBestThumbnailsUrl())
                .channel(snippet.channelTitle)
                .uploadDate(snippet.publishedAt)
                .category(snippet.categoryId)
                .views(formattedViews)
                .likes(formattedLikes)
                .commentCount(commentCount)
                .duration(formattedDuration)
                .subscribers("1.5M") // Dummy
                .engagementRate("4.2") // Dummy
                .seoReport(generateSeoReport(snippet)) // <-- Corrected call
                .build();
    }

    private Video getVideoById(String videoId) {
        VideoApiResponse response = webClientBuilder.baseUrl(baseUrl).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part", "snippet")
                        .queryParam("id", videoId)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(VideoApiResponse.class)
                .block();

        if (response == null || response.items == null || response.items.isEmpty()) {
            return null;
        }
        Snippet snippet = response.items.get(0).snippet;
        return Video.builder()
                .id(videoId)
                .channelTitle(snippet.channelTitle)
                .title(snippet.title)
                .tags(snippet.tags == null ? Collections.emptyList() : snippet.tags)
                .build();

    }

    // Helper Methods
    private String formatCount(long count) {
        if (count < 1000) return String.valueOf(count);
        if (count < 1_000_000) return String.format("%.1fK", count / 1000.0);
        return String.format("%.1fM", count / 1_000_000.0);
    }

    private String formatDuration(String ptDuration) {
        return ptDuration.replace("PT", "").replace("H", ":").replace("M", ":").replace("S", "");
    }

    // --- CONSOLIDATED INNER CLASSES FOR API RESPONSE ---
    // This single set of classes will handle all API responses for this service.

    @Data
    static class SearchApiResponse {
        List<SearchItem> items;
    }

    @Data
    static class SearchItem {
        Id id;
    }

    @Data
    static class Id {
        String videoId;
    }

    @Data
    static class VideoApiResponse {
        List<VideoItem> items;
    }

    @Data
    static class VideoItem {
        Snippet snippet;
        Statistics statistics;
        ContentDetails contentDetails;
    }

    @Data
    static class Snippet {
        String title;
        String description;
        String channelTitle;
        String publishedAt;
        List<String> tags;
        Thumbnails thumbnails;
        String categoryId;
    }

    @Data
    static class Statistics {
        String viewCount;
        String likeCount;
        String commentCount;
    }

    @Data
    static class ContentDetails {
        String duration;
    }

    @Data
    static class Thumbnails {
        ThumbnailsDetails maxres;
        ThumbnailsDetails high;
        ThumbnailsDetails medium;
        ThumbnailsDetails standard;
        ThumbnailsDetails _default; // Use _default as fallback if needed, but standard is better

        String getBestThumbnailsUrl() {
            if (maxres != null) return maxres.url;
            if (standard != null) return standard.url;
            if (high != null) return high.url;
            if (medium != null) return medium.url;
            return _default != null ? _default.url : "";
        }
    }

    @Data
    static class ThumbnailsDetails {
        String url;
        int width;
        int height;
    }
}