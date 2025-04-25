package com.lincpay.chatbot.scheduler;

import com.lincpay.chatbot.dto.response.Response207TxnDataResponseDto;
import com.lincpay.chatbot.entities.TelegramAdminGroup;
import com.lincpay.chatbot.repository.Response207TxnDataRepo;
import com.lincpay.chatbot.serivce.TelegramGroupService;
import com.lincpay.chatbot.serviceimp.TelegramBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

@EnableScheduling
@Configuration
public class NotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);

    @Autowired
    TelegramGroupService telegramGroupService;

    @Autowired
    TelegramBotService telegramBotService;

    @Autowired
    private Response207TxnDataRepo response207Rep;

    @Autowired
    JdbcTemplate jdbcTemplate;

    // Define the threshold for balance exhaustion
    private static final BigDecimal BALANCE_THRESHOLD = BigDecimal.valueOf(1000);

    @Scheduled(fixedRate = 3600000) // 1 hour = 3600000 ms
    public void process207Txn() {
        logger.info("🚀 Scheduler started at {}", new Date());

        try {
            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.HOUR, -1); // Subtract 1 hour
            Date oneHourAgo = cal.getTime();

            List<TelegramAdminGroup> telegramAdminGroups = telegramGroupService.getAllAdminGroupId();
            if (!telegramAdminGroups.isEmpty()) {
                List<Object[]> results = response207Rep.findTxnDetailsBetween(oneHourAgo, now);
                List<Response207TxnDataResponseDto> dtos = new ArrayList<>();
                if (!results.isEmpty()) {
                    for (TelegramAdminGroup adminGroup : telegramAdminGroups) {
                        String chatIdAdmin = adminGroup.getGroupChatId();
                        for (Object[] row : results) {
                            try {
                                Response207TxnDataResponseDto dto = Response207TxnDataResponseDto.builder()
                                        .txnId((String) row[1])
                                        .mid((String) row[2])
                                        .txnDate((Date) row[3])
                                        .txnStatus((String) row[4])
                                        .responseCode((String) row[5])
                                        .createdAt((Date) row[6])
                                        .amount(row[7] != null ? ((Number) row[7]).doubleValue() : null)
                                        .orderNo((String) row[8])
                                        .build();
                                dtos.add(dto);

                                telegramBotService.sendMessage(chatIdAdmin, dto.toString());
//                                logger.info("✅ Sent 207 TXN Alert to chatId: {} for txnId: {}", chatIdAdmin, dto.getTxnId());
                            } catch (Exception e) {
                                logger.error("Error while processing or sending message for row: {}", row, e);
                            }
                        }
                    }
                } else {
                    logger.info("No 207 transactions found between {} and {}", oneHourAgo, now);
                }
            } else {
                logger.warn("No Telegram admin groups found!");
            }

        } catch (Exception e) {
            logger.error("Scheduler execution failed: ", e);
        }

        logger.info("Scheduler ended at {}", new Date());
    }

//    @Scheduled(fixedRate = 300000) // Every 5 minutes (in milliseconds)
//    public void processAlerts() {
//        try {
//            // Get the list of Telegram admin groups
//            List<TelegramAdminGroup> telegramAdminGroups = telegramGroupService.getAllAdminGroupId();
//
//            if (!telegramAdminGroups.isEmpty()) {
//                // Get the pending alerts from the database
//                List<Notification10FailedTxnRequestDto> midsToAlert = getPendingMidsWithDetails();
//
//                // Iterate over each Telegram admin group
//                for (TelegramAdminGroup adminGroup : telegramAdminGroups) {
//                    if (!midsToAlert.isEmpty()) {
//                        // Iterate over the rows fetched from the database
//                        for (Notification10FailedTxnRequestDto alert : midsToAlert) {
//                            try {
//                                // Send the alert using the Telegram bot service
//                                Notification10FailedTxnRequestDto requestDto= new Notification10FailedTxnRequestDto();
//                                requestDto.setMid(alert.getMid());
//                                requestDto.setDate(alert.getDate());
//                                requestDto.setMsg(alert.getMsg());
//                                telegramBotService.sendMessage(adminGroup.getGroupChatId(),requestDto.toString());
//
//                                // Mark the alert as 'processed' in the database
//                                String updateQuery = "UPDATE failure_alert_log SET status = 'processed', last_alert_sent_at = NOW() WHERE mid = ? AND status = 'pending'";
//                                jdbcTemplate.update(updateQuery, alert.getMid());
//
//                            } catch (Exception e) {
//                                // Optional: log the error if sending the alert fails
//                                System.err.println("Failed to send alert for MID: " + alert.getMid());
//                            }
//                        }
//                    } else {
//                        System.out.println("No mids to alert");
//                    }
//                }
//            } else {
//                logger.warn("No Telegram admin groups found!");
//            }
//        } catch (Exception e) {
//            logger.error("Scheduler execution failed: ", e);
//        }
//    }
//
//
//    public List<Notification10FailedTxnRequestDto> getPendingMidsWithDetails() {
//        String sql = "SELECT mid, alert_msg, status, created_at FROM failure_alert_log WHERE status = 'pending'";
//
//        // Use custom RowMapper to map the query result to DTO
//        return jdbcTemplate.query(sql, new Notification10FailedTxnRequestDtoMapper());
//    }
}
