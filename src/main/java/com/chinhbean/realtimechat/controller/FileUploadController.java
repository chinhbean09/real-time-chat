package com.chinhbean.realtimechat.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
public class FileUploadController {

    @Value("${file.upload-dir:/uploads}")
    private String uploadDir;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        // Tạo thư mục upload nếu chưa tồn tại
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Tạo tên file duy nhất
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File destinationFile = new File(uploadDir + File.separator + fileName);

        // Lưu file
        file.transferTo(destinationFile);

        // Trả về URL của file (giả sử ứng dụng chạy trên localhost:8080)
        return "/uploads/" + fileName;
    }
}