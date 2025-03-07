package com.example.demo.controller;

import com.example.demo.model.LoginRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    // 注册用户
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            // 检查是否已存在相同的邮箱
            if (userRepository.findByEmail(user.getEmail()) != null) {
                return ResponseEntity.badRequest().body("邮箱已被注册");
            }

            // 对密码进行加密
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(user.getPassword()));

            // 保存用户到数据库
            userRepository.save(user);
            return ResponseEntity.ok("注册成功!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("注册失败: " + e.getMessage());

        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("邮箱未注册");
        }

        // 生成唯一令牌并设置过期时间（1小时）
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenExpiration(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // 发送邮件
        emailService.sendResetPasswordEmail(user.getEmail(), token);
        return ResponseEntity.ok("重置链接已发送至您的邮箱，请查收！");
    }

    // 重置密码：验证令牌并更新密码
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword
    ) {
        User user = userRepository.findByResetToken(token);

        // 检查令牌是否存在或过期
        if (user == null || user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("链接无效或已过期");
        }

        // 加密并更新密码，清除令牌
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiration(null);
        userRepository.save(user);

        return ResponseEntity.ok("密码重置成功！");
    }
    @GetMapping("/user-email")
    public ResponseEntity<String> getUserEmail(@RequestParam Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户未找到");
            }
            return ResponseEntity.ok(user.getEmail());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取邮箱失败: " + e.getMessage());
        }
    }
    @PostMapping("/send-email")
    public ResponseEntity<?> sendEmail(@RequestBody Map<String, String> request) {
        emailService.sendSimpleMessage(
                request.get("to"),
                request.get("subject"),
                request.get("text")  // 直接取前端传来的text字段
        );
        return ResponseEntity.ok().build();
    }
    // 用户登录（新增代码）
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            // 通过邮箱查找用户
            User user = userRepository.findByEmail(loginRequest.getEmail());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("邮箱未注册");
            }

            // 验证密码
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean isPasswordMatch = encoder.matches(loginRequest.getPassword(), user.getPassword());
            if (!isPasswordMatch) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("密码错误");
            }

            return ResponseEntity.ok("登录成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("登录失败: " + e.getMessage());
        }
    }
}