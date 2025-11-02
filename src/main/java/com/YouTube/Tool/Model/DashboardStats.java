package com.YouTube.Tool.Model;

import com.YouTube.Tool.Entity.ActivityLog;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DashboardStats {
    private long totalTags;
    private long aiRequests;
    private long thumbnailsAnalyzed;
    private long videosAnalyzed;
    private List<ActivityLog> recentActivities;
}