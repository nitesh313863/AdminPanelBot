package com.lincpay.chatbot.dto.Request;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class InitiatedTxnAlertRequestDto {
    private String mid;
    private Timestamp dateTime;
    private String orderNo;
    private String txnId;
    private BigDecimal amount;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("🔔 * Initiated Txn Alert:*\n\n");
        sb.append("🏦 *MID:* ").append(escapeMarkdown(mid)).append("\n")
                .append("💰 *Amount:* ").append(amount != null ? escapeMarkdown(amount.toString()) : "No amount available").append("\n")
                .append("📅 *Create DateTime:* ").append(dateTime != null ? escapeMarkdown(dateTime.toString()) : "No date available").append("\n")
                .append("🛒 *Order No:* ").append(escapeMarkdown(orderNo)).append("\n")
                .append("💳 *Txn ID:* ").append(escapeMarkdown(txnId)).append("\n");

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
