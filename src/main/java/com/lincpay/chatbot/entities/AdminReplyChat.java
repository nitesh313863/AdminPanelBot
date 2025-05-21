package com.lincpay.chatbot.entities;

import lombok.*;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "admin_reply_chat")
public class AdminReplyChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_chat_id")
    private String chatId;

    @Column(name = "admin_reply_msg")
    private String adminReplyMsg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Specifies the date format
    @Column(name = "message_date")
    private LocalDateTime messageDate;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_type") // e.g., text, image, document
    private String fileType;

    @Column(name = "file_name")
    private String fileName;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "file_data")
    private byte[] fileData;


    @Column(name = "content_type")
    private String contentType;

}
