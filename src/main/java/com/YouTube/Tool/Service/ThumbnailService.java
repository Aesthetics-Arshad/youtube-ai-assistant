package com.YouTube.Tool.Service;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ThumbnailService {

    // Patterns ko pehle hi compile karke rakh lo (Best Practice)
    private static final Pattern YOUTUBE_WATCH_PATTERN =
            Pattern.compile("(?:https?:\\/\\/)?(?:www\\.)?youtube\\.com\\/watch\\?v=([a-zA-Z0-9_-]{11})");

    private static final Pattern YOUTUBE_BE_PATTERN =
            Pattern.compile("(?:https?:\\/\\/)?(?:www\\.)?youtu\\.be\\/([a-zA-Z0-9_-]{11})");

    private static final Pattern YOUTUBE_EMBED_PATTERN =
            Pattern.compile("(?:https?:\\/\\/)?(?:www\\.)?youtube\\.com\\/embed\\/([a-zA-Z0-9_-]{11})");

    // Ek array mein saare compiled patterns ko store kar lo
    private static final Pattern[] ALL_PATTERNS = {
            YOUTUBE_WATCH_PATTERN, YOUTUBE_BE_PATTERN, YOUTUBE_EMBED_PATTERN
    };

    public String extractVideoId(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }

        // 1. Check for direct 11-character ID
        if (url.matches("^[a-zA-Z0-9_-]{11}$")) {
            return url;
        }

        // 2. Check against pre-compiled patterns
        for (Pattern pattern : ALL_PATTERNS) {
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1); // Return the captured video ID
            }
        }

        // 3. If no match is found, return null
        return null;
    }
}