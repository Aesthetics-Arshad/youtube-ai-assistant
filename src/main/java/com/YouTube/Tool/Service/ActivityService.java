package com.YouTube.Tool.Service;

import com.YouTube.Tool.Entity.ActivityLog;
import com.YouTube.Tool.Entity.User;
import com.YouTube.Tool.Model.ActivityType;
import com.YouTube.Tool.Repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityLogRepository activityLogRepository;

    public void logActivity(User user, ActivityType type, String details) {
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setActivityType(type);
        log.setDetails(details);
        activityLogRepository.save(log);
    }
}
