package com.lincpay.chatbot.dto.Request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserLoginRequestDto {
    private String username;
    private String password;
}
