package com.lincpay.chatbot.dto.response;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayinLimitExceedResopnsedto {
    private Long id;
    private String company;
    private String sid;
    private String maxTxnAmount;
    private String mid;
    private String remainingLimit;
    private String createDateTime;
    private String updatedDateTime;
    private Long vehicleMasterId;
    private String vehicleName;
    private int masterMarchantid;
    private String masterMarchantName;
    private String merchantVpa;
    private String prifix;
    private String domain;
    private String processor;
    private boolean deleted;



}
