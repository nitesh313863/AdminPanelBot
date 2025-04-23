package com.lincpay.chatbot.serviceimp;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;
import org.springframework.stereotype.Service;

@Service
public class TokenGetAndDecrept {

    public Claims decodeToken(String token, String secret) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)  // Parses the JWT and extracts the claims
                    .getBody();
        } catch (SignatureException e) {
            // Handle invalid token signature
            e.printStackTrace();
        }
        return null;
    }
}
