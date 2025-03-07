package com.example.demo.controller;

import com.example.demo.service.MarkdownConverter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@CrossOrigin
public class ConversionController {

    private final MarkdownConverter converter;

    public ConversionController(MarkdownConverter converter) {
        this.converter = converter;
    }

    @PostMapping(value = "/convert", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> convertMdToPpt(@RequestBody Map<String, String> request) throws IOException {
        String markdownContent = request.get("content");
        File tempFile = converter.convert(markdownContent);
        Path path = Paths.get(tempFile.getAbsolutePath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"presentation.pptx\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(Files.newInputStream(path)));
    }
}
