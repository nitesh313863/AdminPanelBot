package com.lincpay.chatbot.dto.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessageRequestDto {
    private Long id;
    private String chatId;
    private String userName;
    private Long userId;
    private String msgText;
    private String msgtype;
    private String sendertype;
    private String filePath;
    private String fileName;
    private String fileType;
    private String contentType;
    private String caption;
    private String fileData;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime messageDate;
}
