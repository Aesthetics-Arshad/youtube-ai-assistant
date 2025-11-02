package com.YouTube.Tool.Controller;

import com.YouTube.Tool.Entity.User;
import com.YouTube.Tool.Model.UsageData;
import com.YouTube.Tool.Repository.UserRepository;
import com.YouTube.Tool.Service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor // Automatically creates the constructor for dependency injection
public class PageController {

    // Injected services to get user data and dashboard stats
    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    /**
     * This is the NEW, dynamic method for the dashboard.
     * It gets the logged-in user, fetches their stats, and adds them to the model.
     */
    @GetMapping("/dashboard")
    public String dashboardPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Check if a user is actually logged in
        if (userDetails != null) {
            // Find the full User object from the database using the username
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));

            // Get the stats for this specific user
            model.addAttribute("stats", dashboardService.getDashboardStatsForUser(user));
        }
        return "dashboard"; // Return the dashboard.html page with the stats data
    }


    @GetMapping("/api/dashboard/usage-trends")
    @ResponseBody // Tells Spring to return JSON, not an HTML page
    public UsageData getUsageTrends(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            return dashboardService.getUsageTrendsForUser(user);
        }
        // Return empty data if not logged in
        return new UsageData(List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"), List.of(0L, 0L, 0L, 0L, 0L, 0L, 0L));
    }







    @GetMapping("/")
    public String splash() {
        return "splash";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    @GetMapping({"home"})
    public String home(){
        return "home";
    }

    @GetMapping("/video-details")
    public String videoDetails(){
        return "video-details";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}