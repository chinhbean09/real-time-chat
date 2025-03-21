package com.chinhbean.realtimechat.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileUploadController {

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Đổi thư mục lưu file
            String uploadDir = "uploads/images/"; // Thư mục trong project
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    throw new IOException("Failed to create directory: " + uploadDir);
                }
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, file.getBytes());

            File savedFile = new File(filePath.toString());
            if (!savedFile.exists()) {
                throw new IOException("File was not saved: " + filePath);
            }

            String imageUrl = "/images/" + fileName;
            System.out.println("Uploaded file URL: " + imageUrl);
            return imageUrl;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}