package com.example.qa.service.impl;

import com.example.qa.service.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            // 简单实现：返回一个模拟的图片URL
            // 实际项目中应该保存文件到存储服务
            String fileName = UUID.randomUUID().toString() + "." + getFileExtension(file.getOriginalFilename());
            return "/uploads/images/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}