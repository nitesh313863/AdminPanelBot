package com.lincpay.chatbot.repository;

import com.lincpay.chatbot.entities.MerchantChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantChatRepo extends JpaRepository<MerchantChat, Long> {
    List<MerchantChat> findByChatIdAndMessageDateBetweenOrderByMessageDateDesc(String chatId, LocalDateTime start, LocalDateTime end);
    Optional<MerchantChat> findByPhotoFileId(String photoFileId);

    List<MerchantChat> findByPhotoIsNotNull();
}
