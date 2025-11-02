package com.YouTube.Tool.Model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ThumbnailData {
    private String url;
    private int score;
    private boolean hasText;
    private boolean hasFace;
    private boolean highContrast;
    private List<String> recommendations;
}
