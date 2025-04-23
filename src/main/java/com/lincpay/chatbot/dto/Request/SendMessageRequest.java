package com.lincpay.chatbot.dto.Request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SendMessageRequest {
    String chatId;
    String message;
}
