package com.lincpay.chatbot.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TelegramUserResponseDto {
        private String chatId;
        private String userFirstName;
        private String userLastName;
}
