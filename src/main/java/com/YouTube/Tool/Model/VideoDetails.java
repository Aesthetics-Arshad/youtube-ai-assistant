package com.YouTube.Tool.Model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class VideoDetails {

    // Data from API
    private String id;
    private String title;
    private String description;
    private List<String> tags;
    private String thumbnail;
    private String channel;
    private String uploadDate;
    private String category;
    private String views;
    private String likes;
    private long commentCount;
    private String duration;

    // Dummy Data for UI (for now)
    private String subscribers;
    private String engagementRate;
    private int titleScore;
    private int descScore;
    private int tagsScore;
    private SeoReport seoReport;
}