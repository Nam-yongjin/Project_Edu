package com.EduTech.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();

        // 최대 파일 크기 100MB
        factory.setMaxFileSize(DataSize.ofMegabytes(100));
        // 최대 요청 크기 100MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(100));
        // 톰캣 기본 파일 수 제한 해제 (Integer.MAX_VALUE)
        factory.setFileSizeThreshold(DataSize.ofBytes(Integer.MAX_VALUE));

        return factory.createMultipartConfig();
    }
}

