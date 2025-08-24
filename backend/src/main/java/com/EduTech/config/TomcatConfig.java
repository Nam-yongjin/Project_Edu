package com.EduTech.config;

import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class TomcatConfig {

    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();

        factory.addConnectorCustomizers(connector -> {
            connector.setMaxParameterCount(20000);
            connector.setMaxPostSize(500 * 1024 * 1024); // 500MB

            if (connector.getProtocolHandler() instanceof Http11NioProtocol protocol) {
                protocol.setConnectionTimeout(120_000); // 2분
                protocol.setMaxSwallowSize(500 * 1024 * 1024); // 500MB
            }
        });

        factory.addContextCustomizers(context -> {
            // 파일 개수 제한을 context 레벨에서 설정
            context.setAllowMultipleLeadingForwardSlashInPath(true);
        });

        return factory;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(500)); // 파일 최대 500MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(500)); // 요청 최대 500MB
        factory.setFileSizeThreshold(DataSize.ofMegabytes(1)); // 임시 저장 임계치
        return factory.createMultipartConfig();
    }
}
