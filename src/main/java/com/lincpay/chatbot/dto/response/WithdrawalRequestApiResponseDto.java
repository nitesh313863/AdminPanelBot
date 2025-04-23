package com.lincpay.chatbot.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class WithdrawalRequestApiResponseDto {
    public List<WithdrawalData> data;
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Withdrawal Request Details:\n");
        int count = 1;

        if (data == null || data.isEmpty()) {
            sb.append("No withdrawal requests found.\n");
            return sb.toString();
        }

        for (WithdrawalData item : data) {
            sb.append(count++).append(".\n")
                    .append("MID: *").append(item.mid).append("* \n")
                    .append("Company Name: ").append(item.companyName != null ? item.companyName : "N/A").append("\n")
 //                   .append("Withdrawal ID: ").append(item.withdrawalId).append("\n")
                    .append("Amount: ").append(item.amount).append("\n")
                //    .append("Transfer Type: ").append(item.transferType).append("\n")
                    .append("Virtual Account Type: ").append(item.virtualAccountType.isEmpty() ? "N/A" : item.virtualAccountType).append("\n")
                    .append("Create DateTime: ").append(item.createDateTime).append("\n")
                  //  .append("Status: ").append(item.status).append("\n")
                    //.append("Full Name: ").append(item.fullName).append("\n")
                    //.append("Email ID: ").append(item.emailId).append("\n")
                    //.append("UTR: ").append(item.utr != null ? item.utr : "N/A").append("\n")
                    //.append("Transaction Date: ").append(item.transactionDate).append("\n")
                    //.append("Transaction Time: ").append(item.transactionTime != null ? item.transactionTime : "N/A").append("\n")
                    //.append("Is Withdrawal Manual: ").append(item.isWithdrawalManual ? "Yes" : "No").append("\n")
                    // .append("Transfer Mode: ").append(item.transferMode != null ? item.transferMode : "N/A").append("\n")
                    // .append("Is Deposit for Payout: ").append(item.isDepositeForPayout ? "Yes" : "No").append("\n")
                    // .append("Actual Amount: ").append(item.actualAmount).append("\n")
                    // .append("Virtual Account Number: ").append(item.virtualAccountNumber).append("\n")
                    //.append("IFSC: ").append(item.ifsc).append("\n")
                    //.append("Bank Name: ").append(item.bankName).append("\n")
                    //.append("Remark: ").append(item.remark != null ? item.remark : "N/A").append("\n")
                    //.append("Service Charge: ").append(item.serviceCharge).append("\n")
                    //.append("GST Charge: ").append(item.gstCharge).append("\n")
                   
                    // .append("Updated DateTime: ").append(item.updatedDateTime != null ? item.updatedDateTime : "N/A").append("\n")
                    //.append("Updated By: ").append(item.updatedBy != null ? item.updatedBy : "N/A").append("\n")
                    //.append("Created By: ").append(item.createdBy).append("\n")
                    //.append("Vehicle Name: ").append(item.vehicleName != null ? item.vehicleName : "N/A").append("\n")
                    //.append("Create Date: ").append(item.createDate).append("\n")
                    //.append("Create Time: ").append(item.createTime).append("\n")
                    .append("--------------------------------------------------------\n");
        }
        return sb.toString();

    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class WithdrawalData {
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