package com.lincpay.chatbot.serivce;

import com.lincpay.chatbot.dto.Request.UserLoginRequestDto;
import com.lincpay.chatbot.dto.Request.UserRequestDto;
import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.TelegramMerchantGroup;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    ResponseEntity<ResponseModel> addUser(UserRequestDto dto);

    ResponseEntity<ResponseModel<List<TelegramMerchantGroup>>> getAllowedGroupsByUsername(String username);

    ResponseEntity<ResponseModel> validUserData(UserLoginRequestDto dto);
}
