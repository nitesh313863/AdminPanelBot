package com.lincpay.chatbot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GroupMemberResponseDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String username;
    private String privilege;

}
