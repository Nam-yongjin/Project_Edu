package com.EduTech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "mailTaskExecutor")
    public Executor mailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 동시에 발송할 스레드 수
        executor.setMaxPoolSize(10); // 최대 스레드 수 
        executor.setQueueCapacity(50); // 스레드를 사용중일때, 대기 가능한 작업 수, 
        executor.setThreadNamePrefix("MailSender-"); // 스레드 이름 접두사
        executor.initialize(); // 스레드 풀 초기화 (excuter생성)
        return executor;
    }

}
