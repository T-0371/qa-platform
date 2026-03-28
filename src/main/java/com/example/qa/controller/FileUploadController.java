package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@Tag(name = "文件上传", description = "文件上传接口")
public class FileUploadController {

    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @PostMapping("/image")
    @Operation(summary = "上传图片", description = "上传图片文件")
    public ApiResponse<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.error(400, "请选择要上传的文件");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ApiResponse.error(400, "只能上传图片文件");
        }

        // 检查文件大小 (最大5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            return ApiResponse.error(400, "文件大小不能超过5MB");
        }

        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path targetPath = Paths.get(uploadPath, newFilename);
            Files.copy(file.getInputStream(), targetPath);

            // 返回文件URL
            Map<String, String> result = new HashMap<>();
            result.put("url", "/uploads/" + newFilename);
            result.put("filename", newFilename);

            return ApiResponse.success(result);
        } catch (IOException e) {
            return ApiResponse.error(500, "文件上传失败: " + e.getMessage());
        }
    }
}
