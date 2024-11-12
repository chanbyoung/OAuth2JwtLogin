package com.loginStudy.oauth2andJwt.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${file.dir}")
    private String UPLOAD_DIR;
    private static final String FILE_PATH = "/images/**";
    private static final String FILE_PROTOCOL = "file:///";

    /**
     * 외부 URL 요청을 시스템의 지정된 경로에서 제공하도록 설정합니다.
     * 예: /images/** 경로로 요청 시 실제 저장된 이미지 파일을 반환합니다.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(FILE_PATH)
                .addResourceLocations(FILE_PROTOCOL + UPLOAD_DIR); // 실제 이미지 저장 경로
    }
}