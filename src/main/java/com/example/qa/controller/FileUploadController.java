package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/image")
    public ApiResponse<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.error("请选择要上传的文件");
            }
            
            String url = fileUploadService.uploadImage(file);
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", url.substring(url.lastIndexOf("/") + 1));
            return ApiResponse.success("上传成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("上传失败: " + e.getMessage());
        }
    }
}
