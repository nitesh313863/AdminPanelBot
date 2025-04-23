package com.lincpay.chatbot.serivce;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public interface UserValidateService {
    String getUsernameAndPassword(String email,String passPhase);
}
