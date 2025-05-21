package com.lincpay.chatbot.serviceimp;

import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.AdminReplyChat;
import com.lincpay.chatbot.entities.MerchantChat;
import com.lincpay.chatbot.repository.AdminReplyChatRepo;
import com.lincpay.chatbot.repository.MerchantChatRepo;
import com.lincpay.chatbot.serivce.MerchantGroupChatAdminPanelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MerchantGroupChatAdminPanelServiceImp implements MerchantGroupChatAdminPanelService {

    private static final Logger logger = LoggerFactory.getLogger(MerchantGroupChatAdminPanelServiceImp.class);


    @Autowired
    MerchantChatRepo merchantChatRepo;

    @Autowired
    AdminReplyChatRepo adminReplyChatRepo;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseModel> getMerchantChatsByChatId(String chatId) {
        try {
            // Validate the chatId
            if (chatId == null || chatId.isEmpty() || !chatId.startsWith("-")) {
                logger.warn("Invalid Chat ID received: {}", chatId);
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(new ResponseModel("Invalid Chat ID", "Not Accept", HttpStatus.NOT_ACCEPTABLE.value()));
            }

            // Get current time and calculate 24 hours back
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minusHours(24); // Default to last 24 hours

            // Fetch all messages from the last 24 hours (no pagination)
            List<MerchantChat> chats = merchantChatRepo
                    .findByChatIdAndMessageDateBetweenOrderByMessageDateDesc(chatId, start, end);

            // Check if chats exist
            if (chats.isEmpty()) {
                logger.info("No chats found for Chat ID: {}", chatId);
                return ResponseEntity.ok(new ResponseModel("No Chats Found", "not found", HttpStatus.NOT_FOUND.value()));
            }

            // Convert to proper format for frontend
            List<Map<String, Object>> formattedChats = chats.stream()
                    .map(chat -> {
                        Map<String, Object> chatMap = new HashMap<>();
                        chatMap.put("id", chat.getId());
                        chatMap.put("group","merchant");
                        chatMap.put("msgId", chat.getMsgId());
                        chatMap.put("userName", chat.getUserName());
                        chatMap.put("userId", chat.getUserId());
                        chatMap.put("messageDate", convertToDateArray(chat.getMessageDate()));


                        // Check if photo is available, if yes return the base64 string
                        if (chat.getPhoto() != null) {
                            String base64Photo = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(chat.getPhoto());
                            chatMap.put("photo", base64Photo);
                            chatMap.put("messageType", "photo");  // Indicating that this is a photo message
                            chatMap.put("msgType","photo");
                            chatMap.put("senderType","merchant");

                        } else if (chat.getMsgText() != null && !chat.getMsgText().isEmpty()) {
                            // If photo is not available, return the message text
                            chatMap.put("msgText", chat.getMsgText());
                            chatMap.put("msgType","text");
                            chatMap.put("senderType","merchant");
                        }
                        if (chat.getDocFilePath() != null) {
                            String mimeType = chat.getFileType() != null ?
                                    chat.getFileType() : "application/octet-stream";
                            String base64File = "data:" + mimeType + ";base64," +
                                    Base64.getEncoder().encodeToString(chat.getFileData());
                            chatMap.put("fileData", base64File);
                            chatMap.put("msgType","document");
                            chatMap.put("senderType","merchant");
                            chatMap.put("fileName",chat.getFileName());
                            chatMap.put("fileSize",chat.getFileSize());
                        }
                        return chatMap;
                    })
                    .collect(Collectors.toList());

            logger.info("Fetched {} chats for Chat ID: {}", chats.size(), chatId);

            // Prepare response with chat data
            ResponseModel responseModel = new ResponseModel("Chats Fetched", "success", HttpStatus.OK.value(), formattedChats);

            // Return the response
            return ResponseEntity.ok(responseModel);

        } catch (Exception e) {
            logger.error("Exception while fetching merchant chats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel("Error", "Exception Occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }


    @Override
    public void adminReplyStoreDb(String chatId, String msg) {
        try {
            AdminReplyChat adminReplyChat = new AdminReplyChat();
            adminReplyChat.setChatId(chatId);
            adminReplyChat.setAdminReplyMsg(msg);
            adminReplyChat.setMessageDate(LocalDateTime.now());
            adminReplyChat.setFileType("text");
            adminReplyChatRepo.save(adminReplyChat);
        }
        catch (Exception e) {
            logger.error("Exception while saving admin reply chat: {}", e.getMessage(), e);
        }

    }

    @Override
    @Transactional
    public ResponseEntity<ResponseModel> getAdminReplyChat(String chatId) {
        try {
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusHours(24);

            List<AdminReplyChat> chatList = adminReplyChatRepo
                    .findByChatIdAndMessageDateBetweenOrderByMessageDateDesc(chatId, startDate, endDate);

            if (chatList.isEmpty()) {
                return ResponseEntity.ok(
                        new ResponseModel("No chat found for this chatId in last 24 hours", "not found", HttpStatus.OK.value())
                );
            }

            // Format and categorize chats
            List<Map<String, Object>> formattedChats = chatList.stream()
                    .map(chat -> {
                        Map<String, Object> chatMap = new HashMap<>();
                        chatMap.put("chatId", chat.getChatId());
                        chatMap.put("messageDate", convertToDateArray(chat.getMessageDate()));

                        // Categorize based on file type (text, photo, document)
                        if (chat.getFileType() == null || "text".equalsIgnoreCase(chat.getFileType())) {
                            chatMap.put("msgText", chat.getAdminReplyMsg());
                            chatMap.put("messageType", "text");  // Text message
                            chatMap.put("id", chat.getId());
                            chatMap.put("msgType","text");
                            chatMap.put("senderType","admin");
                        } else if ("photo".equalsIgnoreCase(chat.getFileType())) {
                            // For photos, encode them in base64
                            chatMap.put("fileName", chat.getFileName());
                            chatMap.put("contentType", chat.getContentType());
                            chatMap.put("msgId", chat.getId());
                            chatMap.put("msgType","photo");
                            chatMap.put("senderType","admin");
                            chatMap.put("id", chat.getId());
                            if (chat.getFileData() != null) {
                                // Encode image file to base64 if it's available
                                String base64Image = "data:" + chat.getContentType() + ";base64," +
                                        Base64.getEncoder().encodeToString(chat.getFileData());
                                chatMap.put("photo", base64Image);
                            }
                        } else if ("document".equalsIgnoreCase(chat.getFileType())) {

                            // For document (PDF), include the file name and file path for download

                            String mimeType = chat.getContentType() != null ? chat.getContentType() : "application/octet-stream";
                            String base64Data = "data:" + mimeType + ";base64," +
                                    Base64.getEncoder().encodeToString(chat.getFileData());
                            chatMap.put("fileName", chat.getFileName());
                            chatMap.put("contentType", chat.getContentType());
                            chatMap.put("msgId", chat.getId());
                            chatMap.put("fileData", base64Data);
                            chatMap.put("messageType", "document");  // Document message
                            chatMap.put("id", chat.getId());
                        }

                        return chatMap;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new ResponseModel("Admin Reply Chat categorized", "success", HttpStatus.OK.value(), formattedChats)
            );

        } catch (Exception e) {
            logger.error("Exception while getting admin reply chat: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel("Error", "internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }


    @Override
    public void adminReplyStoreDocumentDb(String chatId, MultipartFile file, String type, String contentType, String fileName) throws IOException {
        // Convert the uploaded file to byte[] (binary data)
        byte[] fileBytes = file.getBytes(); // This is the correct byte array for binary storage

        AdminReplyChat adminReplyChat = new AdminReplyChat();
        adminReplyChat.setChatId(chatId);
        adminReplyChat.setFileName(fileName);
        adminReplyChat.setFileType(type);  // e.g., "pdf"
        adminReplyChat.setContentType(contentType); // e.g., "application/pdf"
        adminReplyChat.setMessageDate(LocalDateTime.now());

        // Set the byte array (file data) into the entity
        adminReplyChat.setFileData(fileBytes);

        // Save the entity to the database
        adminReplyChatRepo.save(adminReplyChat);
    }




    // Helper method to convert LocalDateTime to frontend expected format [year, month, day, hour, minute, second]
    private Integer[] convertToDateArray(LocalDateTime dateTime) {
        return new Integer[]{
                dateTime.getYear(),
                dateTime.getMonthValue(),
                dateTime.getDayOfMonth(),
                dateTime.getHour(),
                dateTime.getMinute(),
                dateTime.getSecond()
        };
    }

}
