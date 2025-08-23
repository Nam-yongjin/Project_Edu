package com.EduTech.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.apache.catalina.connector.Connector;

@Configuration
public class TomcatConfig {
    
    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        
        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                // 파일 업로드 제한 설정
                connector.setMaxParameterCount(20000);
                connector.setMaxPostSize(200 * 1024 * 1024); // 200MB
            }
        });
        
        return factory;
    }
}