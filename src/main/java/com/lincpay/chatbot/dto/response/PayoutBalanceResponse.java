package com.lincpay.chatbot.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class PayoutBalanceResponse {
    private String message;
    private int statusCode;
    private ResponseData responseData;
    private ResponseData2 responseData2;
    private String totalAmount;
    private int numberOfTransactions;
    private String txnStatus;
    private String code;


    @Override
    public String toString() {
        return "Payout Balance Response:\n" +
                "------------------------------------------------------\n" +
//                "*  Response Data: * \n" +
                responseData + "\n\n" +
//                "**Additional Data:**\n" +
                responseData2 + "\n\n" ;
//                "Total Amount: * " + (totalAmount != null ? totalAmount : "N/A") + " * \n" +
//                "Number of Transactions: * " + numberOfTransactions + " * \n" +
//                "Transaction Status: * " + (txnStatus != null ? txnStatus : "N/A") + " * \n" +
//                "Code: * " + (code != null ? code : "N/A"+" * ");
    }
}
