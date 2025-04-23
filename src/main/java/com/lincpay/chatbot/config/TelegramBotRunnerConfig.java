package com.lincpay.chatbot.config;

import com.lincpay.chatbot.serviceimp.TelegramBotService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotRunnerConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBotService telegramBotService) throws Exception {
        // Use your custom DefaultBotSession to create TelegramBotsApi
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        // Register the bot with long polling
        botsApi.registerBot(telegramBotService);

        // Register custom commands (if required)
        telegramBotService.registerCommandsForAll();

        return botsApi;
    }
}
