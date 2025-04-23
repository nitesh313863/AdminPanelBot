package com.lincpay.chatbot.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResponseData2 {
    private String upiAmount;
    private String wallet;
    private String impsAmount;
    @Override
    public String toString() {
        return  "UPI Balance: * " + upiAmount + " * \n" +
//                "Wallet Balance: * " + wallet + " * \n" +
                "Payout IMPS: * " + impsAmount+" * ";
    }
}
