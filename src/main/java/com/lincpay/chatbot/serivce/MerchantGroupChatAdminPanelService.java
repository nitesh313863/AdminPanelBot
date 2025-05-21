package com.lincpay.chatbot.serivce;

import com.lincpay.chatbot.dto.response.ResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public interface MerchantGroupChatAdminPanelService {
    public ResponseEntity<ResponseModel> getMerchantChatsByChatId(String chatId);

    void adminReplyStoreDb(String chatId, String msg);

    ResponseEntity<ResponseModel> getAdminReplyChat(String chatId);

    void adminReplyStoreDocumentDb(String chatId, MultipartFile file, String photos, String contentType,String originalFileName ) throws IOException;
}
