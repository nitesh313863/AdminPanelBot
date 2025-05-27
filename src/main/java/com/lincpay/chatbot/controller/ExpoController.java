package com.lincpay.chatbot.controller;

import com.lincpay.chatbot.entities.ExpoToken;
import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.TelegramMerchantGroup;
import com.lincpay.chatbot.entities.User;
import com.lincpay.chatbot.repository.ExpoRepo;
import com.lincpay.chatbot.repository.TelegramMerchantGroupRepo;
import com.lincpay.chatbot.repository.UserRepo;
import com.lincpay.chatbot.serivce.ExpoService;
import com.lincpay.chatbot.serviceimp.MerchantGroupChatAdminPanelServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/expo")
@CrossOrigin("*")
public class ExpoController {
    @Autowired
    ExpoService expoService;
    @Autowired
    TelegramMerchantGroupRepo telegramMerchantGroupRepo;
    private static final Logger logger = LoggerFactory.getLogger(ExpoController.class);


    @PostMapping("token-store")
    public ResponseEntity<ResponseModel> storeToken(@RequestBody ExpoToken dto) {
        return expoService.storeExpoToken(dto);
    }


}
