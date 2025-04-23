package com.lincpay.chatbot.serviceimp;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lincpay.chatbot.dto.response.FetchMidDataResponse;
import com.lincpay.chatbot.dto.response.PayinLimitResponseListDto;
import com.lincpay.chatbot.dto.response.PayinSidLimtReciveRespose;
import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.dto.response.TelegramAdminBotMidResponse;
import com.lincpay.chatbot.dto.response.WithdrawalRequestApiResponseDto;
@Service
public class ApiCallerService {

    private static final Logger logger = LoggerFactory.getLogger(ApiCallerService.class);
    
    @Value("${telegram.url.pendingWithdrawal}")
	private String url;
    
    @Value("${telegram.url.combine}")
	private String combineurl;
    
    @Value("${telegram.url.sid}")
	private String sidurl;
    
    @Value("${telegram.url.withdrawal.approve.url}")
	private String withdrawalurl;
    
    @Value("${telegram.url.fetch.mid.url}")
   	private String midurl;
    
    public Object callApi(String token) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
          //  String url = "http://localhost:9092/merchant/withdrawal/getAllPandingWithdrawal";
            

            headers.set("Authentication", "Bearer " + token);
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            HttpEntity<String> entity = new HttpEntity<>(null, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(response.getBody(), WithdrawalRequestApiResponseDto.class);
            } else {
                System.err.println("Unexpected API response: " + response.getStatusCode());
                return response.getStatusCode();
            }

        } catch (HttpStatusCodeException e) {
            return e.getStatusCode().value();
        } catch (Exception e) {
            System.err.println("API Call Exception: " + e.getMessage());
            return null;
        }
    }

    private String formatResponseAsTable(String jsonResponse) {
        try {
            StringBuilder response = new StringBuilder();
            JSONObject json = new JSONObject(jsonResponse);

            // Ensure 'data' is present and not null
            if (!json.has("data") || json.isNull("data")) {
                return "⚠️ No data found.";
            }

            Object data = json.get("data");

            response.append("📋 *Response Details:*\n\n");

            // Handle JSONObject and JSONArray types for 'data'
            if (data instanceof JSONObject) {
                appendDataBlock(response, (JSONObject) data);
            } else if (data instanceof JSONArray) {
                JSONArray dataArray = (JSONArray) data;
                for (int i = 0; i < dataArray.length(); i++) {
                    appendDataBlock(response, dataArray.getJSONObject(i));
                    if (i < dataArray.length() - 1) {
                        response.append("\n──────────────\n"); // Separator between transactions
                    }
                }
            }

            return response.toString();
        } catch (Exception e) {
            // Add a more detailed error message for debugging
            e.printStackTrace();
            return "⚠️ Error formatting response: " + e.getMessage();
        }
    }

    // Appends a transaction block with key-value format
    private void appendDataBlock(StringBuilder response, JSONObject obj) {
        for (String key : obj.keySet()) {
            String value = obj.optString(key, "-");
            response.append("🔹 *").append(capitalizeKey(key)).append(":* ")
                    .append(value.equals("-") ? "-" : value).append("\n");
        }
    }

    // Capitalizes the first letter of each key for better readability
    private String capitalizeKey(String key) {
        if (key == null || key.isEmpty()) return key;
        return key.substring(0, 1).toUpperCase() + key.substring(1);
    }
//    public Object payoutBalanceApi(String mid, String token) {
//        try {
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//
//            // Correct authentication header key
//            headers.set("Authentication", "Bearer " + token);
//            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
//            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
//
//            // Construct URL with path variable
//            String url = "http://localhost:9092/merchant/dashboard/getamountdata/{mid}";
//
//            HttpEntity<String> entity = new HttpEntity<>(null, headers);
//            ResponseEntity<String> response = restTemplate.exchange(
//                    url,
//                    HttpMethod.GET,
//                    entity,
//                    String.class,
//                    mid  // Passing mid as a path variable
//            );
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            return objectMapper.readValue(response.getBody(), PayoutBalanceResponse.class);
//
//        } catch (HttpStatusCodeException e) {
//            logger.error("API Call Error: {} - Response: {}", e.getStatusCode(), e.getResponseBodyAsString());
//            return e.getStatusCode().value();
//        } catch (Exception e) {
//            logger.error("API Call Error: {}", e.getMessage());
//        }
//        return null;  // Return null if the request fails
//    }
//    
    public String callCombinedApi(String mid, String token ,String BusinessName) {
        try {
        	
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            // Correct authentication header key
            headers.set("Authentication", "Bearer " + token);
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            // ✅ Create request payload as a Map
            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("mid", mid);
            requestPayload.put("timeFrame", "1 day"); // Default value

            // ✅ Send POST request with headers and payload
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestPayload, headers);
//            String url = "https://api.godemo.in/MerchantAdminPanel/telegram-api/payoutBlanceTodayByMid";

            // ✅ Make the API call
            ResponseEntity<String> response = restTemplate.exchange(
                    combineurl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // ✅ Check for successful response
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseData = objectMapper.readValue(response.getBody(), Map.class);

                // ✅ Check if response contains data
                if (responseData != null && responseData.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) responseData.get("data");

                    // ✅ Extract required fields with fallback/default values
                    BigDecimal midDeposit = data.get("midDeposit") != null ? new BigDecimal(data.get("midDeposit").toString()) : BigDecimal.ZERO;
                    String companyName = BusinessName;
                    BigDecimal holdAmount = data.get("holdAmount") != null ? new BigDecimal(data.get("holdAmount").toString()) : BigDecimal.ZERO;
                    BigDecimal upiBalance = data.get("upiBalance") != null ? new BigDecimal(data.get("upiBalance").toString()) : BigDecimal.ZERO;
                    BigDecimal payoutBalance = data.get("payoutBalance") != null ? new BigDecimal(data.get("payoutBalance").toString()) : BigDecimal.ZERO;

                    // ✅ Format the response as a string
                    String formattedResponse = String.format(
                            "Payout Balance Today:\n" +
                                    "------------------------------------------------------\n" +
                            		"MID : %s\n"+
                                    "Deposit Amount:  %s\n" +
                                    "Company Name:  %s\n" +
                                    "Hold Amount:  %s\n\n" +
                                    "UPI Balance:  %s\n" +
                                    "Payout IMPS:  %s",
                            mid,midDeposit, companyName, holdAmount, upiBalance, payoutBalance
                    );

                    // ✅ Return the formatted response
                    return formattedResponse;
                } else {
                    return "No data found for the given MID.";
                }
            } else {
                return "API call failed with status: " + response.getStatusCode();
            }

        } catch (HttpStatusCodeException e) {
            logger.error("API Call Error: {} - Response: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return "API Call Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (Exception e) {
            logger.error("API Call Error: {}", e.getMessage());
            return "API Call Error: " + e.getMessage();
        }
    }



    public Object payinLimitExceeds(String token) {
        try {
            RestTemplate restTemplate=new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authentication", "Bearer " + token);
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            String url = "https://api.godemo.in/MerchantAdminPanel/admin/sidMaster/getAllSid";

            logger.info("Request URL: {}", url);
            logger.info("Headers: {}", headers);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.getBody(), PayinLimitResponseListDto.class);

        } catch (HttpStatusCodeException e) {
            logger.error("API Call Error: {} - Response: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return e.getStatusCode().value();
        } catch (Exception e) {
            logger.error("API Call Error: {}", e.getMessage());
        }
        return null; // You can return an error DTO instead of null
    }

    public ResponseModel withdrawalRequestAction(String token, String remark, String status, String transactionDate, String utr, int withdrawalId) {
    //    String url = "http://localhost:9092/merchant/withdrawal/updateWithdrawal";
//        String url = "https://api.godemo.in/MerchantAdminPanel/merchant/withdrawal/updateWithdrawal";

        // Prepare request headers
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authentication", "Bearer " + token);
        headers.set("accept", "*/*");
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("isWithdrawalManual", false);
        requestBody.put("remark", remark);
        requestBody.put("status", status);
        requestBody.put("transactionDate", transactionDate);
        requestBody.put("utr", utr);
        requestBody.put("withdrawalId", withdrawalId);

        // Create HTTP request
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // Send PUT request and parse response into ResponseModel
            ResponseEntity<ResponseModel> response = restTemplate.exchange(withdrawalurl, HttpMethod.PUT, requestEntity, ResponseModel.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                return new ResponseModel("Withdrawal Approval Failed", "failed", 400, "Error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel("Internal Server Error", "failed", 500, e.getMessage());
        }
    }



    public List<FetchMidDataResponse> fetchMid(String token) {
        try {
//            String url = "https://api.godemo.in/MerchantAdminPanel/telegram-api/fetchAllMid";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authentication", "Bearer " + token);
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    midurl, HttpMethod.GET, entity, String.class
            );

            if (response.getBody() == null) {
                System.err.println("API Response is empty.");
                return Collections.emptyList();
            }

            // Parse the response into the wrapper class
            ObjectMapper objectMapper = new ObjectMapper();
            TelegramAdminBotMidResponse wrapper =
                    objectMapper.readValue(response.getBody(), TelegramAdminBotMidResponse.class);

            if (wrapper.getData() == null || wrapper.getData().isEmpty()) {
                System.err.println("No MID data found.");
                return Collections.emptyList();
            }

            return wrapper.getData();

        } catch (HttpStatusCodeException e) {
        	logger.error("API Error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
        	logger.error("API Call Error: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    public ResponseModel<List<PayinSidLimtReciveRespose>> payinLimtSid(String mid, String token) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authentication", "Bearer " + token);
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            // Correct URL for @RequestParam
//            String url = "https://api.godemo.in/MerchantAdminPanel/admin/sidMaster/getSidByMid?mid={mid}";
           
            HttpEntity<String> entity = new HttpEntity<>(null, headers);

            // Make API call and get raw response
            ResponseEntity<ResponseModel> response = restTemplate.exchange(
                    sidurl,
                    HttpMethod.GET,
                    entity,
                    ResponseModel.class,
                    mid
            );

            // If the response is successful, convert LinkedHashMap to PayinSidLimtReciveRespose
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ResponseModel<?> rawResponseModel = response.getBody();

                // Convert the data part (List<LinkedHashMap>) to List<PayinSidLimtReciveRespose>
                List<?> rawData = (List<?>) rawResponseModel.getData();
                ObjectMapper mapper = new ObjectMapper();

                // Convert each LinkedHashMap to PayinSidLimtReciveRespose
                List<PayinSidLimtReciveRespose> convertedList = rawData.stream()
                        .map(item -> mapper.convertValue(item, PayinSidLimtReciveRespose.class))
                        .collect(Collectors.toList());

                // Return the correctly formatted response model
                return new ResponseModel<>("Success", "SUCCESS", 200, convertedList);
            } else {
                return new ResponseModel<>("No data found", "FAILED", 404, null);
            }
        } catch (HttpStatusCodeException e) {
            logger.error("API Error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return new ResponseModel<>("Error", "FAILED", e.getStatusCode().value(), null);
        } catch (Exception e) {
            logger.error("API Call Error: {}", e.getMessage());
            return new ResponseModel<>("Internal Server Error", "FAILED", 500, null);
        }
    }

}
