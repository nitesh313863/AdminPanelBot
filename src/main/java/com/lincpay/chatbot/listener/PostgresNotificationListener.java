package com.lincpay.chatbot.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lincpay.chatbot.dto.Request.Notification10FailedTxnRequestDto;
import com.lincpay.chatbot.entities.TelegramAdminGroup;
import com.lincpay.chatbot.serivce.TelegramGroupService;
import com.lincpay.chatbot.serviceimp.TelegramBotService;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class PostgresNotificationListener {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TelegramBotService telegramBotService;

    @Autowired
    private TelegramGroupService telegramGroupService;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void startListening() {
        executorService.submit(() -> {
            try (Connection conn = dataSource.getConnection()) {
                log.info("Successfully connected to the PostgreSQL database."); // Log connection success
                PGConnection pgConn = conn.unwrap(PGConnection.class);
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("LISTEN failed_txn_alert");
                }
                ObjectMapper objectMapper = new ObjectMapper();
                while (true) {
                    PGNotification[] notifications = pgConn.getNotifications(5000); // wait 5s
                    if (notifications != null) {
                        List<TelegramAdminGroup> telegramAdminGroups = telegramGroupService.getAllAdminGroupId();
                        if (telegramAdminGroups.isEmpty()) {
                            log.warn("No admin groups found.");
                            continue;
                        }
                        for (PGNotification notification : notifications) {
                            String json = notification.getParameter();
                            log.info("Received NOTIFY: {}", json);
                            try {
                                Notification10FailedTxnRequestDto dto =
                                        objectMapper.readValue(json, Notification10FailedTxnRequestDto.class);

                                for (TelegramAdminGroup group : telegramAdminGroups) {
                                    telegramBotService.sendMessage(group.getGroupChatId(), dto.toString());
                                }
                            } catch (Exception e) {
                                log.error("Failed to parse notification payload: {}", json, e);
                            }
                        }
                    }
                    Thread.sleep(1000); // avoid tight loop
                }
            } catch (Exception e) {
                log.error("Event Listener for 10 txn alert failed", e);
            }
        });
    }
}
