package com.YouTube.Tool.Service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest // Spring Boot ko batata hai ki yeh ek test class hai
class ThumbnailServiceTest {

    @Autowired // Test ke liye ThumbnailService ka bean inject karo
    private ThumbnailService thumbnailService;

    @Test // JUnit ko batata hai ki yeh ek test case hai
    void testExtractVideoId_WithStandardUrl_ShouldReturnCorrectId() {
        // 1. Arrange (Setup)
        String standardUrl = "https://www.youtube.com/watch?v=N8Oyy2UNhYo";
        String expectedId = "N8Oyy2UNhYo";

        // 2. Act (Action)
        String actualId = thumbnailService.extractVideoId(standardUrl);

        // 3. Assert (Check)
        Assertions.assertEquals(expectedId, actualId);
    }

    @Test
    void testExtractVideoId_WithShortUrl_ShouldReturnCorrectId() {
        // Arrange
        String shortUrl = "https://youtu.be/N8Oyy2UNhYo";
        String expectedId = "N8Oyy2UNhYo";

        // Act
        String actualId = thumbnailService.extractVideoId(shortUrl);

        // Assert
        Assertions.assertEquals(expectedId, actualId);
    }

    @Test
    void testExtractVideoId_WithEmbedUrl_ShouldReturnCorrectId() {
        // Arrange
        String embedUrl = "https://www.youtube.com/embed/N8Oyy2UNhYo";
        String expectedId = "N8Oyy2UNhYo";

        // Act
        String actualId = thumbnailService.extractVideoId(embedUrl);

        // Assert
        Assertions.assertEquals(expectedId, actualId);
    }

    @Test
    void testExtractVideoId_WithDirectId_ShouldReturnCorrectId() {
        // Arrange
        String directId = "N8Oyy2UNhYo";
        String expectedId = "N8Oyy2UNhYo";

        // Act
        String actualId = thumbnailService.extractVideoId(directId);

        // Assert
        Assertions.assertEquals(expectedId, actualId);
    }

    @Test
    void testExtractVideoId_WithInvalidUrl_ShouldReturnNull() {
        // Arrange
        String invalidUrl = "https://www.google.com";

        // Act
        String actualId = thumbnailService.extractVideoId(invalidUrl);

        // Assert
        Assertions.assertNull(actualId);
    }
}
