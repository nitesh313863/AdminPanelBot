package com.lincpay.chatbot.dto.Request;

import com.lincpay.chatbot.util.AmountToWordsUtil;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class GetPayinSidLimit {
    private String mid;
//    private String sidCompanyName;
    private String sid;
    private BigDecimal sidLimit;
    private String routingType;
    private Integer prority;
    private String domain;
    private boolean status;
    private boolean flag;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("🔔 *The Payin SID limit has been changed by the admin.:*\n\n");
        sb.append("🏦 *MID:* ").append(escapeMarkdown(mid)).append("\n")
 //         .append("🏢 *Company Name:* ").append(sidCompanyName != null ? escapeMarkdown(sidCompanyName) : "N/A").append("\n")
          .append("🔢 *SID:* ").append(escapeMarkdown(sid)).append("\n")
          .append("💰 *SID Limit:* ").append(sidLimit != null ? sidLimit.toPlainString() : "0.00").append("\n")
                .append("\uD83D\uDCDC *InWordAmount: *").append(AmountToWordsUtil.convertToIndianCurrencyWords(sidLimit)).append("\n")
          .append("🔄 *Routing Type:* ").append(escapeMarkdown(routingType)).append("\n")
          .append("📊 *Priority:* ").append(prority != null ? prority : "N/A").append("\n")
          .append("🌍 *Domain:* ").append(escapeMarkdown(domain)).append("\n")
          .append("✅ *Status:* ").append(status ? "Active ✅" : "Inactive ❌").append("\n")
          .append("🚩 *Flag:* ").append(flag ? "Enabled 🚀" : "Disabled ⛔").append("\n");

        return sb.toString();
    }

    private String escapeMarkdown(String text) {
        if (text == null) return "";
        return text.replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("`", "\\`");
    }
}
