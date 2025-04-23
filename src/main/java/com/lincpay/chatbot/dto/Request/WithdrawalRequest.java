package com.lincpay.chatbot.dto.Request;
import java.util.List;


public class WithdrawalRequest {
    public List<WithdrawalRequestRequest> data;
    public String toString() {
        StringBuilder sb = new StringBuilder("Withdrawal Request Details:\n");
        int count = 1;

        if (data == null || data.isEmpty()) {
            sb.append("No withdrawal requests found.\n");
            return sb.toString();
        }

        for (WithdrawalRequestRequest item : data) {
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
