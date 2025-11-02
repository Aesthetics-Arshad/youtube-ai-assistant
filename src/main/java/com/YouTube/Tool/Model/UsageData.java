package com.YouTube.Tool.Model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UsageData {
    private List<String> labels;
    private List<Long> counts;
}
