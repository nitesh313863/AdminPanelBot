package com.lincpay.chatbot.dto.response;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelegramAdminBotMidResponse {
    private String message;
    private String status;
    private int statusCode;
    private List<FetchMidDataResponse> data;  // Matches "data" field in response
}

