package com.lincpay.chatbot.serviceimp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lincpay.chatbot.serivce.UserValidateService;

@Service
public class UserValidateServiceImp implements UserValidateService {

    // ✅ Logger for monitoring and troubleshooting
    private static final Logger logger = LoggerFactory.getLogger(UserValidateServiceImp.class);


    @Value("${telegram.login.url}")
    private String loginUrl;


    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * ✅ Get the token (username and password) from the external authentication API.
     *
     * @param email     User's email address.
     * @param passPhase User's passphrase.
     * @return A valid token or error message if failed.
     */
    @Override
    public String getUsernameAndPassword(String email, String passPhase) {
        logger.info("Authenticating user with email: {}", email);
        // ✅ Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ✅ Create an empty request (no body for GET)
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            // ✅ Prepare the URL dynamically with path variables
            logger.info("Request URL: {}", String.format(loginUrl, email, passPhase));

            // ✅ Send request and capture response
            // ✅ Send request with path variables
            ResponseEntity<String> response = restTemplate.exchange(
                    loginUrl, HttpMethod.GET, request, String.class, email, passPhase
            );

            // ✅ Check if response is successful
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String extractedToken = extractToken(response.getBody());

                if ("No Record Found".equalsIgnoreCase(extractedToken)) {
                    logger.warn("No record found for user: {}", email);
                    return null;
                }
                logger.info("Authentication successful for user: {}", email);
                return extractedToken;
            } else {
                logger.warn("Authentication failed with status: {}", response.getStatusCode());
                return null;
            }

        } catch (ResourceAccessException e) {
            // 🛑 Handle unreachable or server down errors
            logger.error("Authentication server is unreachable: {}", e.getMessage());
            return "500"; // Return 500 error as a string

        } catch (HttpClientErrorException e) {
            // 🛑 Handle 4xx errors (e.g., 401 Unauthorized, 404 Not Found)
            logger.error("API returned client error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;

        } catch (Exception e) {
            // 🛑 Handle any unexpected errors
            logger.error("Error during authentication: {}", e.getMessage(), e);
            return "500"; // Return generic 500 error
        }
    }

    /**
     * ✅ Extract token from the JSON API response.
     *
     * @param jsonResponse API response in JSON format.
     * @return Extracted token or null if parsing fails.
     */
    private String extractToken(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // ✅ Extract token from "data" field
            String token = rootNode.path("data").asText();
            logger.info("Token extracted successfully.");
            return token;
        } catch (Exception e) {
            logger.error("Error parsing token from API response: {}", e.getMessage(), e);
            return null;
        }
    }
}
