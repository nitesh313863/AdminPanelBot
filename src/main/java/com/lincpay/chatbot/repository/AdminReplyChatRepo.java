package com.lincpay.chatbot.repository;

import com.lincpay.chatbot.entities.AdminReplyChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdminReplyChatRepo extends JpaRepository<AdminReplyChat, Long> {
    List<AdminReplyChat> findByChatIdAndMessageDateBetweenOrderByMessageDateDesc(String chatId, LocalDateTime start, LocalDateTime end);
}
