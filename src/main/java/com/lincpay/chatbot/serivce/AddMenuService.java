package com.lincpay.chatbot.serivce;

import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.AddMenuTelegramAdminPanel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AddMenuService {
    ResponseEntity<ResponseModel> addMenue(AddMenuTelegramAdminPanel addMenuTelegram);
    public List<AddMenuTelegramAdminPanel> findAllMenuAdmin();
}
