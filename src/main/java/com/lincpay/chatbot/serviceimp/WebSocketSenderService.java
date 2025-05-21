package com.lincpay.chatbot.serviceimp;
import com.lincpay.chatbot.entities.ExpoToken;
import com.lincpay.chatbot.entities.MerchantChat;
import com.lincpay.chatbot.entities.TelegramMerchantGroup;
import com.lincpay.chatbot.repository.ExpoRepo;
import com.lincpay.chatbot.repository.TelegramMerchantGroupRepo;
import com.lincpay.chatbot.repository.UserRepo;
import com.lincpay.chatbot.serivce.ExpoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WebSocketSenderService {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketSenderService.class);

    @Autowired
    ExpoRepo expoRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    TelegramMerchantGroupRepo telegramMerchantGroupRepo;

    @Autowired
    ExpoPushNotificationService expoPushNotificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendMerchantGroupMessage(MerchantChat chat) {
        try {
            Map<String, Object> chatMap = new HashMap<>();

            // Add all basic fields
            chatMap.put("id", chat.getId());
            chatMap.put("chatId", chat.getChatId());
            chatMap.put("userName", chat.getUserName());
            chatMap.put("userId", chat.getUserId());
            chatMap.put("msgId", chat.getMsgId());
            chatMap.put("msgText", chat.getMsgText());
            chatMap.put("caption", chat.getCaption());
            chatMap.put("fileType", chat.getFileType());
            chatMap.put("photoFileId", chat.getPhotoFileId());
            chatMap.put("docFileId", chat.getDocFileId());
            chatMap.put("fileName", chat.getFileName());
            chatMap.put("docFilePath", chat.getDocFilePath());
            chatMap.put("messageDate", chat.getMessageDate() != null ?
                    chat.getMessageDate().toString() : null);

            // Handle photo data
            if(chat.getMsgText() != null) {
                chatMap.put("msgText", chat.getMsgText());
                chatMap.put("msgType","text");
                chatMap.put("senderType","merchant");
  //              expoNotification(chat.getChatId(),chat.getMsgText());
            }
            if (chat.getPhoto() != null && chat.getPhoto().length > 0) {
                String mimeType = chat.getFileType() != null ?
                        chat.getFileType() : "image/jpeg";
                String base64Photo = "data:" + mimeType + ";base64," +
                        Base64.getEncoder().encodeToString(chat.getPhoto());
                chatMap.put("photo", base64Photo);
                chatMap.put("msgType","photo");
                chatMap.put("senderType","merchant");
            }

            // Handle file/document data
            if (chat.getFileData() != null && chat.getFileData().length > 0) {
                String mimeType = chat.getFileType() != null ?
                        chat.getFileType() : "application/octet-stream";
                String base64File = "data:" + mimeType + ";base64," +
                        Base64.getEncoder().encodeToString(chat.getFileData());
                chatMap.put("fileData", base64File);
                chatMap.put("msgType","document");
                chatMap.put("senderType","merchant");
                chatMap.put("fileSize", chat.getFileSize());
            }

            // Send the complete message
            messagingTemplate.convertAndSend("/topic/merchant-group", chatMap);

        } catch (Exception e) {
            logger.error("Error sending WebSocket message for chat ID: " + chat.getId(), e);
            // You might want to implement retry logic here

        }
    }

    private void expoNotification(String groupId, String msgText) {
        // Validate inputs
        if (groupId == null || groupId.trim().isEmpty()) {
            logger.warn("Empty groupId provided");
            return;
        }
        if (msgText == null || msgText.trim().isEmpty()) {
            logger.warn("Empty message text provided");
            return;
        }

        try {
            // Find the group
            TelegramMerchantGroup telegramMerchantGroup = telegramMerchantGroupRepo.findByGroupChatId(groupId);
            if (telegramMerchantGroup == null) {
                logger.warn("No group found with groupChatId: {}", groupId);
                return;
            }

            // Get user IDs in the group
            List<Long> userIds = userRepo.findUserIdsByGroupId(telegramMerchantGroup.getId());
            if (userIds == null || userIds.isEmpty()) {
                logger.info("No users found in group with ID: {}", telegramMerchantGroup.getId());
                return;
            }

            // Process tokens and send notifications
            int successCount = 0;
            int failureCount = 0;

            for (Long userId : userIds) {
                try {
                    List<ExpoToken> tokens = expoRepo.findByUserIds(userId);
                    for (ExpoToken token : tokens) {
                        try {
                            if (token.getExpoToken() != null && !token.getExpoToken().isEmpty()) {
                                logger.debug("Sending notification to token: {} for user: {}",
                                        token.getExpoToken(), userId);
                                expoPushNotificationService.sendNotification("new message",
                                        msgText,
                                        token.getExpoToken());
                                successCount++;
                            }
                        } catch (Exception e) {
                            failureCount++;
                            logger.error("Failed to send notification to token: {} for user: {}",
                                    token.getExpoToken(), userId, e);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error fetching tokens for user ID: {}", userId, e);
                    // Continue processing other users
                }
            }

            // Log summary
            if (successCount == 0 && failureCount == 0) {
                logger.info("No valid tokens found for any users in group: {}", groupId);
            } else {
                logger.info("Notification sending completed. Success: {}, Failed: {}",
                        successCount, failureCount);
            }

        } catch (Exception e) {
            logger.error("Unexpected error processing groupId: {}", groupId, e);
        }
    }
}
