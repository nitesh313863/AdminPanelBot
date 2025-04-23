package com.lincpay.chatbot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response207TxnDataResponseDto {
    private String txnId;
    private String mid;
    private Date txnDate;
    private String responseCode;
    private Date createdAt;
    private Double amount;
    private String orderNo;
    private String txnStatus;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("🚨 *New 207 Response Transaction Detected*\n\n");

        sb.append("🏦 *MID:* ").append(escapeMarkdown(mid)).append("\n")
                .append("🧾 *TxnID:* ").append(escapeMarkdown(txnId)).append("\n")
                .append("💰 *Amount:* ").append(amount != null ? amount.toString() : "N/A").append("\n")
                .append("🛒 *OrderNo:* ").append(escapeMarkdown(orderNo)).append("\n")
                .append("🕒 *TxnDate:* ").append(txnDate != null ? txnDate.toString() : "N/A").append("\n")
                .append("📟 *ResponseCode:* ").append(escapeMarkdown(responseCode)).append("\n")
                .append("📶 *TxnStatus:* ").append(escapeMarkdown(txnStatus)).append("\n")
                .append("🗓️ *CreatedAt:* ").append(createdAt != null ? createdAt.toString() : "N/A").append("\n");
        sb.append("\n⚠️ *Action may be required. Please verify this transaction.*");

        return sb.toString();
    }


    // Markdown escaping to prevent Telegram format issues
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
