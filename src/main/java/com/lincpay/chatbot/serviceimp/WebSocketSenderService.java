package com.lincpay.chatbot.serviceimp;
import com.lincpay.chatbot.entities.MerchantChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebSocketSenderService {

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
    //        logger.error("Error sending WebSocket message for chat ID: " + chat.getId(), e);
            // You might want to implement retry logic here

        }
    }


}
