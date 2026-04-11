package com.example.qa.service.impl;

import com.cloudinary.Cloudinary;
import com.example.qa.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private Cloudinary cloudinary;

    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            // 将MultipartFile转换为File
            File tempFile = convertMultipartFileToFile(file);
            
            // 上传到Cloudinary
            Map<String, Object> params = new HashMap<>();
            params.put("folder", "qa-platform");
            params.put("public_id", UUID.randomUUID().toString());
            params.put("resource_type", "image");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(tempFile, params);
            
            // 删除临时文件
            tempFile.delete();
            
            // 返回Cloudinary提供的URL
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws Exception {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(multipartFile.getBytes());
        }
        return convFile;
    }
}