package com.lincpay.chatbot.dto.Request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayoutInsufficientBalanceRequestDto {
    private String mid;
    private String merchantName;
    private BigDecimal currentBalance;
    private BigDecimal requestedAmount;
    private String currency;
    private String dateTime;
    private String message; // optional remark

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("🚫 *Payout Request Failed: Insufficient Balance*\n\n");
        String displayCurrency = currency != null ? currency : "INR";

        sb.append("🏦 *MID:* ").append(escapeMarkdown(mid)).append("\n")
                .append("🏢 *Merchant:* ").append(escapeMarkdown(merchantName)).append("\n")
                .append("💸 *Requested Payout:* ").append(requestedAmount != null ? requestedAmount.toPlainString() : "0.00")
                .append(" ").append(displayCurrency).append("\n")
                .append("💰 *Current Balance:* ").append(currentBalance != null ? currentBalance.toPlainString() : "0.00")
                .append(" ").append(displayCurrency).append("\n")
                .append("🕒 *Timestamp:* ").append(dateTime).append("\n");

        if (message != null && !message.isEmpty()) {
            sb.append("📝 *Message:* ").append(escapeMarkdown(message)).append("\n");
        } else {
            sb.append("❗ *Alert:* Balance is insufficient for this Merchant.\n");
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
