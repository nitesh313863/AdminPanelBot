package com.lincpay.chatbot.dto.Request;

import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayinLimitExceedRequestDto {
    private String mid;
    private String sid;
    private BigDecimal maxTxnAmount;
    private BigDecimal remainingLimit;
    private BigDecimal requestedAmount;
    private String message;
    private String dateTime;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("🔔 *Pay-in Limit Exceeded Notification*\n\n");
        sb.append("🏦 *MID:* ").append(escapeMarkdown(mid)).append("\n")
                .append("🔢 *SID:* ").append(escapeMarkdown(sid)).append("\n")
                .append("💳 *Requested Amount:* ").append(requestedAmount != null ? requestedAmount.toPlainString() : "N/A").append("\n")
                .append("💰 *Max Txn Amount:* ").append(maxTxnAmount != null ? maxTxnAmount.toPlainString() : "0.00").append("\n")
                .append("💸 *Remaining Limit:* ").append(remainingLimit != null ? remainingLimit.toPlainString() : "0.00").append("\n")
                .append("🕒 *Timestamp:* ").append(dateTime).append("\n");

        if (message != null) {
            sb.append("📝 *Message:* ").append(escapeMarkdown(message)).append("\n");
        }

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
