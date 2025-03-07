package com.example.demo.service;

import com.example.demo.model.SlideContent;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MarkdownConverterServiceImpl implements MarkdownConverter {

    @Override
    public File convert(String content) throws IOException {
        List<SlideContent> slides = parseMarkdown(content);
        return createPresentation(slides);
    }


    @Override
    public List<SlideContent> parseMarkdown(String content) {
        List<SlideContent> slides = new ArrayList<>();
        SlideContent currentSlide = null;

        String[] lines = content.split("\\r?\\n");
        for (String line : lines) {
            if (line.startsWith("#")) {
                currentSlide = new SlideContent();
                int level = line.lastIndexOf('#') + 1;
                currentSlide.setLevel(level);
                currentSlide.setTitle(line.replaceAll("#+", "").trim());
                slides.add(currentSlide);
            } else if (currentSlide != null && !line.trim().isEmpty()) {
                currentSlide.addContent(line.trim());
            }
        }
        return slides;
    }
    @Override
    public File createPresentation(List<SlideContent> slides) throws IOException {
        XMLSlideShow ppt = new XMLSlideShow();

        // 设置默认样式
        XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
        XSLFTextShape titlePlaceholder = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT).getPlaceholder(0);
        XSLFTextParagraph titlePara = titlePlaceholder.getTextParagraphs().get(0);
        titlePara.setTextAlign(TextParagraph.TextAlign.CENTER);

        for (SlideContent slideContent : slides) {
            XSLFSlideLayout layout = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT);
            XSLFSlide slide = ppt.createSlide(layout);

            // 设置标题
            XSLFTextShape titleShape = slide.getPlaceholder(0);
            titleShape.clearText();

            XSLFTextParagraph titleParaNew = titleShape.addNewTextParagraph();
            titleParaNew.setTextAlign(TextParagraph.TextAlign.CENTER);  // 段落对齐

            XSLFTextRun titleRun = titleParaNew.addNewTextRun();        // 创建文本运行
            titleRun.setText(slideContent.getTitle());
            titleRun.setFontSize(36.0);                             // 正确设置字号
            titleRun.setFontFamily("Arial");                        // 设置字体

            // 设置内容
            XSLFTextShape contentShape = slide.getPlaceholder(1);
            contentShape.clearText();
            for (String line : slideContent.getContent()) {
                XSLFTextParagraph para = contentShape.addNewTextParagraph();
                para.setTextAlign(TextParagraph.TextAlign.LEFT);     // 段落对齐

                XSLFTextRun run = para.addNewTextRun();              // 创建文本运行
                run.setText(line);
                run.setFontSize(18.0);                               // 正确设置字号
                run.setFontColor(new Color(0, 0, 0));                // 设置字体颜色
            }
        }

        // 保存临时文件
        File tempFile = File.createTempFile("presentation", ".pptx");
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            ppt.write(out);
        }
        return tempFile;
    }
}
