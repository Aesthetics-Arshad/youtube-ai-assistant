package com.YouTube.Tool.Repository;

import com.YouTube.Tool.Entity.ActivityLog;
import com.YouTube.Tool.Entity.User;
import com.YouTube.Tool.Model.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // Method to count activities of a specific type for a user
    long countByUserAndActivityType(User user, ActivityType type);

    // Method to find the 5 most recent activities for a user
    List<ActivityLog> findFirst5ByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT FUNCTION('DATE_FORMAT', a.createdAt, '%Y-%m-%d') as day, COUNT(a) as count " +
            "FROM ActivityLog a " +
            "WHERE a.user = :user AND a.createdAt >= :startDate " +
            "GROUP BY day ORDER BY day ASC")
    List<Object[]> countActivitiesByUserGroupByDay(@Param("user") User user, @Param("startDate") LocalDateTime startDate);


}
