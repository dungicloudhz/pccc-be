package com.cozyquoteforge.pccc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allow-credentials:false}")
    private boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")           // ✅ TẤT CẢ origins
                .allowedMethods("*")                   // ✅ TẤT CẢ methods
                .allowedHeaders("*")                   // ✅ TẤT CẢ headers
                .allowCredentials(false)               // ✅ Không cần credentials
                .maxAge(3600);
    }
}
