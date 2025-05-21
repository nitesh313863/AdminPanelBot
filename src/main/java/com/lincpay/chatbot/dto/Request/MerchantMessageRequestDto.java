package com.lincpay.chatbot.dto.Request;


import lombok.*;

@Getter
@Setter
public class MerchantMessageRequestDto {
    private String chatId;
    private String message;
    private String username;  // logged-in user's name
}
