package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final String DEFAULT_FROM = "1262079013@qq.com";

    @Autowired
    private JavaMailSender mailSender;

    // 原有的密码重置邮件发送方法
    public void sendResetPasswordEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(DEFAULT_FROM);
        message.setTo(to);
        message.setSubject("密码重置请求");
        message.setText("请点击以下链接重置密码（1小时内有效）:\n"
                + "http://jm1037510ci5.vicp.fun/reset-password.html?token=" + token);
        mailSender.send(message);
    }

    // 新增的通用文本邮件发送方法
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(DEFAULT_FROM);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}