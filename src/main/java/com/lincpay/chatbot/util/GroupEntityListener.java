//package com.lincpay.chatbot.util;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.lincpay.chatbot.entities.TelegramMerchantGroup;
//import com.lincpay.chatbot.serviceimp.TelegramBotService;
//
//import jakarta.persistence.PostPersist;
//import jakarta.persistence.PostRemove;
//import jakarta.persistence.PostUpdate;
//
//@Component
//public class GroupEntityListener {
//    @Autowired
//    private TelegramBotService botService;
//
//    @PostPersist
//    @PostUpdate
//    @PostRemove
//    public void onAdminGroupChange(TelegramMerchantGroup group) {
//        botService.loadTelelgramMerchantGroup();  // Auto-refresh admin group IDs
//    }
//
//}
