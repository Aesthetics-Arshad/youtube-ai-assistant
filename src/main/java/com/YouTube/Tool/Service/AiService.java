package com.YouTube.Tool.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ChatClient chatClient;

    public AiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String generateTitles(String videoIdea) {
        String prompt = """
            You are a YouTube title generation machine. Your only job is to generate titles.
            Generate exactly 5 creative, SEO-optimized, and click-worthy YouTube video titles for the following topic.
            
            Rules:
            - DO NOT provide any explanation, introduction, or conclusion.
            - Provide ONLY the list of 5 titles.
            - Each title must start with a number followed by a period (e.g., "1. My Title").
            - Each title must be on a new line.

            Topic: {videoIdea}
            """;

        return chatClient.prompt()
                .user(userSpec -> userSpec.text(prompt).param("videoIdea", videoIdea))
                .call()
                .content();
    }

    // --- YEH NAYA METHOD ADD HUA ---
    public String generateDescription(String videoIdea) {
        String prompt = """
            You are a helpful YouTube assistant that writes engaging video descriptions.
            Write a 3-paragraph YouTube video description for the following video idea.
            
            Rules:
            - The first paragraph should be a catchy hook to grab the viewer's attention.
            - The second paragraph should briefly describe what the video is about.
            - The third paragraph should be a call-to-action (e.g., "Subscribe for more!", "Check out my other videos!").
            - End the response with 3 relevant hashtags.
            - DO NOT provide any extra commentary.

            Video Idea: {videoIdea}
            """;

        return chatClient.prompt()
                .user(userSpec -> userSpec.text(prompt).param("videoIdea", videoIdea))
                .call()
                .content();
    }

    // --- YEH NAYA METHOD BHI ADD HUA ---
    public String generateTags(String videoIdea) {
        String prompt = """
            You are a YouTube SEO expert. Your only job is to generate a list of tags.
            Generate a list of 20 high-impact SEO tags for a video about the following topic.
            
            Rules:
            - DO NOT provide any explanation or numbering.
            - Provide ONLY a comma-separated list of tags (e.g., tag1, tag2, tag3).

            Topic: {videoIdea}
            """;

        return chatClient.prompt()
                .user(userSpec -> userSpec.text(prompt).param("videoIdea", videoIdea))
                .call()
                .content();
    }
}