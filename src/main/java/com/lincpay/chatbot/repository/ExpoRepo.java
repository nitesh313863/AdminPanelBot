package com.lincpay.chatbot.repository;

import com.lincpay.chatbot.entities.ExpoToken;
import com.lincpay.chatbot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpoRepo extends JpaRepository<ExpoToken,Long> {

    Optional<ExpoToken> findByUserId(Long userId);
    @Query("SELECT e FROM ExpoToken e WHERE e.userId = :userId")
    List<ExpoToken> findByUserIds(@Param("userId") Long userId);

}
