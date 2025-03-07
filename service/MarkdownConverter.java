package com.example.demo.service;

import com.example.demo.model.SlideContent;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface MarkdownConverter {
    File convert(String content) throws IOException;
    List<SlideContent> parseMarkdown(String content);
    File createPresentation(List<SlideContent> slides) throws IOException;
}
