package com.lincpay.chatbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);  // Minimum threads
        executor.setMaxPoolSize(100);   // Maximum threads
        executor.setQueueCapacity(500); // Task queue size
        executor.setThreadNamePrefix("BotThread-");
        executor.initialize();
        return executor;
    }
}
