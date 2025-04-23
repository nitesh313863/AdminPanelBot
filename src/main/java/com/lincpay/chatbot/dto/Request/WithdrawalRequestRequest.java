package com.lincpay.chatbot.dto.Request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WithdrawalRequestRequest {
    public String mid;
    public long withdrawalId;
    public BigDecimal amount;
    public String transferType;
    public String virtualAccountType;
    public String status;
    public String fullName;
    public String emailId;
    public String utr;
    public String transactionDate;
    public String transactionTime;
    public boolean isWithdrawalManual;
    public String transferMode;
    public boolean isDepositeForPayout;
    public BigDecimal actualAmount;
    public String virtualAccountNumber;
    public String ifsc;
    public String bankName;
    public String remark;
    public BigDecimal serviceCharge;
    public BigDecimal gstCharge;
    public String createDateTime;
    public String companyName;
    public String updatedDateTime;
    public String updatedBy;
    public String createdBy;
    public String vehicleName;
    public String createDate;
    public String createTime;
}
