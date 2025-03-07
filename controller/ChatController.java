package com.example.demo.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import java.util.*;

@Controller
public class ChatController {

    private final String API_URL = "https://api.coze.cn/open_api/v2/chat";
    private final String BOT_ID = "7476746959580512291";
    private final String API_KEY = "Bearer pat_1zjzz88B2dItkjIcrRdMMuviYyE1I8FoEyipLJgEUWw25CzUl2UaMHVAB10kr71O";

    @GetMapping("/")
    public String index() {
        return "chat";
    }

    @PostMapping("/ask")
    public String askCoze(@RequestParam("question") String question, Model model) {
        if (question == null || question.trim().isEmpty()) {
            model.addAttribute("error", "请输入问题内容！");
            return "chat";
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("bot_id", BOT_ID);
        requestBody.put("user", "user_example");
        requestBody.put("query", question);
        requestBody.put("stream", false);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response;

        try {
            response = restTemplate.exchange(API_URL, HttpMethod.POST, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseData = response.getBody();
                if (responseData != null && responseData.containsKey("messages")) {
                    List<Map<String, Object>> messages = (List<Map<String, Object>>) responseData.get("messages");

                    List<String> responseMessages = new ArrayList<>();
                    for (Map<String, Object> message : messages) {
                        Object content = message.get("content");
                        if (content instanceof String) {
                            // 按换行符 `\n` 或句号 `。` 进行分段
                            responseMessages.addAll(Arrays.asList(((String) content).split("\\n|。")));
                        }
                    }

                    model.addAttribute("responses", responseMessages);
                }
            } else {
                model.addAttribute("error", "API 请求失败，HTTP 状态码：" + response.getStatusCode());
            }
        } catch (Exception e) {
            model.addAttribute("error", "请求失败: " + e.getMessage());
        }

        return "chat";
    }
}