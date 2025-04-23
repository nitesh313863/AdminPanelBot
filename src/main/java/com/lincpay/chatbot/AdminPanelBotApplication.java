package com.lincpay.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync(proxyTargetClass = true) // Enable async processing with class-based proxies
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AdminPanelBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminPanelBotApplication.class, args);
	}

}
