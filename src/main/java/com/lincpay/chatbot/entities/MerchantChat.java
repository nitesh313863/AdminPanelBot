package com.lincpay.chatbot.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
public class MerchantChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatId;
    private String userName;
    private Long userId;
    private Integer msgId;
    private String msgText;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "photo")
    private byte[] photo;

    private String photoFileId;

    private String caption;

    private String fileType; // optional metadata (e.g. "image/jpeg", "application/pdf")

    private String docFileId;  // for PDF and document messages
    private String fileName;   // original file name (e.g. invoice.pdf)

    private String docFilePath; // optional, if you download and store file locally

    private LocalDateTime messageDate;
    private Integer fileSize;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file_data")
    private byte[] fileData; // stores PDF/photo as bytes

}
