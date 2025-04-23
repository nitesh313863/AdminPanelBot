package com.lincpay.chatbot.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResponseData {
    private String settlmentAmout;
    private String depositAmount;
    private String totalCommisionEarnedAmount;
    private String chragebackAmount;
    private String holdAmount;
    private String withdrawalAmount;
    private String payAmount;
    private String totalwalletBalance;
    private String securityDepositAmount;
    private String guranteeAmount;
    private String dropCreditAmount;
    private String companyName;

    @Override
    public String toString() {
        return  //"Settlement Amount: * " + settlmentAmout + "* \n" +
                "Deposit Amount: * " + depositAmount + " * \n" +
                "Company Name: * " + companyName + " * \n" +
                //"Total Commission Earned: * " + totalCommisionEarnedAmount + " * \n" +
//                "Chargeback Amount: * " + chragebackAmount + " * \n" +
                "Hold Amount: * " + holdAmount + " * \n" ;
               // "Withdrawal Amount: * " + withdrawalAmount + " * \n" +
                //"Pay Amount: * " + payAmount + " * \n" +
                //"Total Wallet Balance: * " + totalwalletBalance + " * \n" +
                //"Security Deposit Amount: * " + securityDepositAmount + " * \n" +
                //"Guarantee Amount: * " + guranteeAmount + " * \n" +
                //"Drop Credit Amount: * " + dropCreditAmount+" * ";
    }

}
