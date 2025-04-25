package com.lincpay.chatbot.dto.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Data
public class Notification10FailedTxnRequestDto {
    private String mid;
    private String msg;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("🚨 *Failed Transaction Alert*\n\n");
        sb.append("🏦 *MID:* ").append(escapeMarkdown(mid)).append("\n")
                .append("📝 *Message:* ").append(msg != null ? escapeMarkdown(msg) : "No message available").append("\n")
                .append("🕒 *Create DateTime:* ")
                .append(escapeMarkdown(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .append("\n\n")
                .append("📣 *Note:* Please investigate the issue promptly.");
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
