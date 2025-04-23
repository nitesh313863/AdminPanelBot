package com.lincpay.chatbot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FetchResponseAdminGroupMember {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String status;
    private Boolean isBot;

}
