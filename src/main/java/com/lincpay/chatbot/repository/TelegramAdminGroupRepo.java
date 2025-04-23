package com.lincpay.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lincpay.chatbot.entities.TelegramAdminGroup;

public interface TelegramAdminGroupRepo extends JpaRepository<TelegramAdminGroup,Integer> {

}
