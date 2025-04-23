package com.lincpay.chatbot.repository;

import com.lincpay.chatbot.entities.TelegramGroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramGroupMessageRepo extends JpaRepository<TelegramGroupMessage,Long> {

}
