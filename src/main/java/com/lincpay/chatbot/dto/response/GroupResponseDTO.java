package com.lincpay.chatbot.dto.response;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponseDTO {
    private Long id;
    private String groupName;
    private Long groupId;
    private LocalDateTime createdDate;
    private String mid;
    private int totalMembers;
}