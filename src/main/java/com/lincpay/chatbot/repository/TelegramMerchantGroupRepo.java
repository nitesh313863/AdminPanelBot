package com.lincpay.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lincpay.chatbot.entities.TelegramMerchantGroup;

@Repository
public interface TelegramMerchantGroupRepo extends JpaRepository<TelegramMerchantGroup, Integer> {

    TelegramMerchantGroup findByGroupChatId(String groupChatId);

	TelegramMerchantGroup findByMid(String mid);
}
