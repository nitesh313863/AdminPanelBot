package com.lincpay.chatbot.dto.Request;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Notification10FailedTxnRequestDto {
    private String mid;
    private String msg;
    private Timestamp date;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("🔔 *10 Failed Txn Alert:*\n\n");
        sb.append("🏦 *MID:* ").append(escapeMarkdown(mid)).append("\n")
                .append("🏢 *Message:* ").append(msg != null ? escapeMarkdown(msg) : "No message available").append("\n")
                .append("📅 *Create DateTime:* ").append(date != null ? escapeMarkdown(date.toString()) : "No date available").append("\n");

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
