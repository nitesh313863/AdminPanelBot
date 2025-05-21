package com.lincpay.chatbot.dto.Request;

import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReplyMessageDto {
    private String chatId;
    private String text;
    private Integer replyToMessageId;
}
