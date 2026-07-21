package com.vn.test.bshoes.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Local image upload for admin screens (e.g. product variant image) that don't
 * want to depend on the external image-picker library. Accepts small JPG/PNG
 * files and serves them back out under /api/uploads/** (see WebConfig).
 */
@RestController
public class UploadController {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png");
    private static final long MAX_SIZE_BYTES = 1_048_576L; // 1MB

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping(value = "/api/upload", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Chưa chọn tệp ảnh");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType) || file.getSize() >= MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("Chỉ chấp nhận ảnh JPG/PNG dưới 1MB");
        }

        try {
            Path dir = Path.of(uploadDir);
            Files.createDirectories(dir);

            String ext = "image/png".equals(contentType) ? ".png" : ".jpg";
            String filename = UUID.randomUUID() + ext;
            Path target = dir.resolve(filename);
            Files.write(target, file.getBytes());

            return ResponseEntity.ok(Map.of("url", "/api/uploads/" + filename));
        } catch (IOException e) {
            throw new UncheckedIOException("Không thể lưu ảnh", e);
        }
    }
}
