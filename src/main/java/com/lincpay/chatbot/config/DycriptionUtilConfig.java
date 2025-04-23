package com.lincpay.chatbot.config;

import com.lincpay.chatbot.serviceimp.TokenGetAndDecrept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;

@Service
public class DycriptionUtilConfig {

    private String secret = "jwttoken";  // Secret key used to decode the token

    @Autowired
    private TokenGetAndDecrept tokenGetAndDecrept;

    /**
     * Extracts the "prefix" claim from the provided JWT token.
     * @param jwt The JWT token string.
     * @return The extracted prefix or an empty string if extraction fails.
     */
    public String extractPrefixFromToken(String jwt) {
        try {
            // Decode the token to get the claims
            Claims claims = tokenGetAndDecrept.decodeToken(jwt, secret);

            if (claims != null) {

                return claims.get("mid", String.class);  // Return the "prefix" claim
            }
        } catch (Exception e) {
            // Handle exceptions if necessary
            e.printStackTrace();
        }
        return "";  // Return empty string if prefix extraction fails
    }


}
