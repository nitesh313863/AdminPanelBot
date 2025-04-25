package com.lincpay.chatbot.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawalResponseDto {
    private String mid;
    private String companyName;
    private BigDecimal amount;
    private String virtualAccountType;
    private String createDateTime;
    public Integer withdrawalId;
    public String amountInWord;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("🔔 *Withdrawal Request Notification:*\n\n");
        sb.append("🏦 *MID:* ").append(escapeMarkdown(mid)).append("\n")
                .append("🏢 *Company Name:* ").append(companyName != null ? escapeMarkdown(companyName) : "N/A").append("\n")
                .append("💰 *Amount:* ").append(amount != null ? amount.toPlainString() : "0.00").append("\n")
                .append("\uD83D\uDCDC *InWordAmount:* ").append(escapeMarkdown(amountInWord)).append("\n")
                .append("🔄 *Virtual Account Type:* ")
                .append((virtualAccountType != null && !virtualAccountType.isEmpty()) ? escapeMarkdown(virtualAccountType) : "N/A").append("\n")
                .append("📅 *Create DateTime:* ").append(escapeMarkdown(createDateTime)).append("\n");

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
