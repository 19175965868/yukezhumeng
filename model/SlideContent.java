package com.example.demo.model;
import java.util.ArrayList;
import java.util.List;

public class SlideContent {
    private int level;
    private String title;
    private List<String> content = new ArrayList<>();

    // Getters and Setters
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<String> getContent() { return content; }
    public void addContent(String text) { this.content.add(text); }
}