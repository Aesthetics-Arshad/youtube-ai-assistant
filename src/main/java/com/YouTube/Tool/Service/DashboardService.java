package com.YouTube.Tool.Service;

import com.YouTube.Tool.Entity.User;
import com.YouTube.Tool.Model.ActivityType;
import com.YouTube.Tool.Model.DashboardStats;
import com.YouTube.Tool.Model.UsageData;
import com.YouTube.Tool.Repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ActivityLogRepository activityLogRepository;

    public DashboardStats getDashboardStatsForUser(User user) {
        long tags = activityLogRepository.countByUserAndActivityType(user, ActivityType.TAG_GENERATION);
        long ai = activityLogRepository.countByUserAndActivityType(user, ActivityType.AI_ASSISTANT_USE);
        long thumbnails = activityLogRepository.countByUserAndActivityType(user, ActivityType.THUMBNAIL_ANALYSIS);
        long videos = activityLogRepository.countByUserAndActivityType(user, ActivityType.VIDEO_DETAILS_EXTRACTION);

        return DashboardStats.builder()
                .totalTags(tags)
                .aiRequests(ai)
                .thumbnailsAnalyzed(thumbnails)
                .videosAnalyzed(videos)
                .recentActivities(activityLogRepository.findFirst5ByUserOrderByCreatedAtDesc(user))
                .build();
    }

    public UsageData getUsageTrendsForUser(User user) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(6).withHour(0).withMinute(0).withSecond(0);
        List<Object[]> results = activityLogRepository.countActivitiesByUserGroupByDay(user, sevenDaysAgo);

        // Convert raw results (["2025-10-18", 5]) into a Map (Date -> Count)
        Map<String, Long> countsByDayString = results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        // Create labels and counts for the last 7 days, filling missing days with 0
        Map<String, Long> finalCounts = new LinkedHashMap<>(); // Use LinkedHashMap to preserve order
        DateTimeFormatter dbDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD
        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("EEE"); // Mon, Tue etc.

        for (int i = 6; i >= 0; i--) {
            LocalDateTime day = LocalDateTime.now().minusDays(i);
            String dayString = day.format(dbDateFormatter);
            String dayLabel = day.format(labelFormatter); // Get day name like "Mon"
            finalCounts.put(dayLabel, countsByDayString.getOrDefault(dayString, 0L));
        }

        return new UsageData(new ArrayList<>(finalCounts.keySet()), new ArrayList<>(finalCounts.values()));
    }


}