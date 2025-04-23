package com.lincpay.chatbot.dto.Request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawalRequestReceive {
    private String mid;
    private String companyName;
    private BigDecimal amount;
    private String virtualAccountType;
    private String createDateTime;
    public Integer withdrawalId;

}
