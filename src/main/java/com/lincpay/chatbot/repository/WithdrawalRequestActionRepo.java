package com.lincpay.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lincpay.chatbot.entities.WithdrawalRequestAction;

@Repository
public interface WithdrawalRequestActionRepo extends JpaRepository<WithdrawalRequestAction, Long> {

}
