package com.example.ui.ui.stretchmarket;

import java.util.List;

public class StretchItem {
    private String name;
    private String englishName;
    private List<String> steps;

    public StretchItem(String name, String englishName, List<String> steps) {
        this.name = name;
        this.englishName = englishName;
        this.steps = steps;
    }

    public String getName() { return name; }
    public String getEnglishName() { return englishName; }
    public List<String> getSteps() { return steps; }
}