package com.YouTube.Tool.Model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SeoReport {
    // REPLACED overallScore with individual scores
    private int titleScore;
    private int descriptionScore;
    private int tagsScore;

    private List<ChecklistItem> checklist;

    @Data
    @Builder
    public static class ChecklistItem {
        private String text;
        private boolean passed;
        private String priority; // "HIGH", "MEDIUM", "LOW"
    }
}