package com.lincpay.chatbot.serivce;

import com.lincpay.chatbot.entities.ExpoToken;
import com.lincpay.chatbot.dto.response.ResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ExpoService {
    public ResponseEntity<ResponseModel> storeExpoToken(ExpoToken dto);
}
