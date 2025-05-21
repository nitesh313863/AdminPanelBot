package com.lincpay.chatbot.dto.Request;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserRequestDto {
    private String username;
    private String password;
    private String email;
    private String phone;

    // IDs of merchant groups this user should have access to
    private Set<Integer> allowedGroupIds;
}
