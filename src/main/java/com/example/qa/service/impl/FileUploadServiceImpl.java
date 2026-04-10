package com.example.qa.service.impl;

import com.example.qa.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            // 创建上传目录
            File uploadDir = new File(uploadPath + "images/");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + "." + getFileExtension(file.getOriginalFilename());
            String filePath = uploadPath + "images/" + fileName;

            // 保存文件
            file.transferTo(new File(filePath));

            // 返回相对路径，前端可以通过 /uploads 访问
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