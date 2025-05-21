package com.lincpay.chatbot.serviceimp;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class ExpoPushNotificationService {

    private static final String EXPO_API_URL = "https://exp.host/--/api/v2/push/send";
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendNotification(String expoToken, String title, String body) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("to", expoToken);
            message.put("title", title);
            message.put("body", body);
            message.put("sound", "default"); // Optional
            message.put("priority", "high");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");

            HttpEntity<List<Map<String, Object>>> request = new HttpEntity<>(Collections.singletonList(message), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(EXPO_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Push notification sent successfully!");
            } else {
                System.out.println("Failed to send push: " + response.getBody());
            }

        } catch (Exception e) {
            System.err.println("Error sending push notification: " + e.getMessage());
        }
    }
}

