package com.lincpay.chatbot.dto.Request;

import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendAlertMeassegeInMidGroup {
	private String mid;
	private String alertMesage;
	private String remark;
	private String date;
	private String time;	
	
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder("🔔 *Alert Message Details:*\n\n");
	    sb.append("🏦 *MID:* ").append(escapeMarkdown(mid)).append("\n")
	      .append("⚠️ *Alert Message:* ").append(alertMesage != null ? escapeMarkdown(alertMesage) : "N/A").append("\n")
	      .append("📝 *Remark:* ").append(remark != null ? escapeMarkdown(remark) : "N/A").append("\n")
	      .append("📅 *Date:* ").append(date != null ? escapeMarkdown(date) : "N/A").append("\n")
	      .append("⏰ *Time:* ").append(time != null ? escapeMarkdown(time) : "N/A").append("\n");

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
