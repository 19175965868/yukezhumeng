package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ProfileController {

    @Value("${upload.dir}")
    private String uploadDir;

    // 显示个人资料修改表单
    @GetMapping("/profile")
    public String showProfileForm(Model model) {
        // 将重定向到静态资源文件
// 获取用户邮箱的头像文件名
        String email = "user@example.com";  // 这个值应该从用户会话或数据库中获取
        String fileName = email + ".jpg"; // 假设头像文件名是邮箱+“.jpg”
        String fileUrl = "/user/uploads/" + email + ".jpg"; // 生成头像文件的访问路径

        // 将头像的 URL 传递到模板
        model.addAttribute("profileImage", fileUrl);
        return "redirect:/profile.html"; // 直接重定向到 static/profile.html
    }

    // 处理头像上传
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("email") String email,
                                   @RequestParam("imageUpload") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "未选择文件！");
            return "redirect:/profile.html";  // 返回 profile.html 页面
        }

        try {
            // 获取文件扩展名
            String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String newFileName = email + fileExtension; // 使用邮箱创建文件名

            // 定义目标文件路径
            Path targetLocation = Paths.get(uploadDir + newFileName);

            // 删除之前的文件（如果存在）
            File targetFile = targetLocation.toFile();
            if (targetFile.exists()) {
                targetFile.delete();
            }
            String avatarUrl = "/user/uploads/" + newFileName;
            // 上传文件
            Files.copy(file.getInputStream(), targetLocation);

            redirectAttributes.addFlashAttribute("message", "文件上传成功！");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "文件上传失败！");
            e.printStackTrace();
        }

        return "redirect:/profile.html";  // 返回 profile.html 页面
    }
    @GetMapping("/uploads/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        Resource resource = new FileSystemResource(filePath);

        if (resource.exists() || resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}