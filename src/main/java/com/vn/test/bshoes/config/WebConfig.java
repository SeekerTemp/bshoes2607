package com.vn.test.bshoes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Serves locally-uploaded files (see UploadController) back out over HTTP.
 * Mapped under /api/uploads/** so the Vite dev proxy (/api -> :8085) already
 * routes these requests to the backend without any extra proxy config.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + uploadDir + "/";
        registry.addResourceHandler("/api/uploads/**").addResourceLocations(location);
    }
}
