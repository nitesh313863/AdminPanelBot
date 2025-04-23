package com.lincpay.chatbot.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayinLimitResponseListDto {
    private List<PayinLimitExceedResopnsedto> data;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("* Sid Master Details * :\n");

        int count = 1;
        for (PayinLimitExceedResopnsedto item : data) {
            sb.append("Entry #").append(count++).append("\n");
            sb.append("---------------------------------------------------------\n");
 //           sb.append(String.format("id: %d\n", item.getId()));
            sb.append(String.format("sid: %s\n", item.getSid()));
            sb.append(String.format("mid: %s\n", item.getMid()));
            //sb.append(String.format("company: %s\n", item.getCompany()));     
            sb.append(String.format("maxTxnAmount: %s\n", item.getMaxTxnAmount()));      
            sb.append(String.format("remainingLimit: %s\n", item.getRemainingLimit() != null ? item.getRemainingLimit() : "null"));
//            sb.append(String.format("createDateTime: %s\n", item.getCreateDateTime()));//           sb.append(String.format("updatedDateTime: %s\n", item.getUpdatedDateTime()));
//            sb.append(String.format("vehicleMasterId: %d\n", item.getVehicleMasterId()));
//            sb.append(String.format("vehicleName: %s\n", item.getVehicleName()));
//            sb.append(String.format("masterMarchantid: %d\n", item.getMasterMarchantid()));
         //   sb.append(String.format("masterMarchantName: %s\n", item.getMasterMarchantName()));
//            sb.append(String.format("merchantVpa: %s\n", item.getMerchantVpa()));
//            sb.append(String.format("prifix: %s\n", item.getPrifix()));
            sb.append(String.format("domain: %s\n", item.getDomain() != null ? item.getDomain() : "null"));
          //  sb.append(String.format("processor: %s\n", item.getProcessor() != null ? item.getProcessor() : "null"));
            sb.append(String.format("deleted: %s\n", item.isDeleted()));
            sb.append("---------------------------------------------------------\n");
            
        }
        return sb.toString();
    }

}
