package com.vn.test.bshoes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Serves locally-uploaded files (see UploadController) back out over HTTP, and
 * declares the CORS policy for /api.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Origins allowed to call the API from a browser. Override on another machine
     * with e.g. -Dapp.cors.origins=http://192.168.1.50:5173
     */
    @Value("${app.cors.origins:http://localhost:5173,http://127.0.0.1:5173,http://localhost:4173,http://127.0.0.1:4173}")
    private String[] corsOrigins;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + uploadDir + "/";
        registry.addResourceHandler("/api/uploads/**").addResourceLocations(location);
    }

    /**
     * The documented dev flow does NOT need this: `npm run dev` (5173) and
     * `npm run preview` (4173) both proxy /api to :8085, so the browser only ever
     * sees same-origin requests (see frontend/vite.config.js).
     *
     * It matters the moment anyone bypasses that proxy — opening the built SPA
     * from another port/host, or pointing a tool straight at the API. Since login
     * now sends X-Auth-Token, a NON-simple header, such a request triggers a CORS
     * preflight; without this mapping the preflight fails and every authenticated
     * call dies with an opaque network error rather than a useful status.
     *
     * allowCredentials stays off on purpose: auth rides in a header, not a cookie,
     * so there is no reason to let browsers attach credentials cross-origin.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(corsOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "X-Auth-Token")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
