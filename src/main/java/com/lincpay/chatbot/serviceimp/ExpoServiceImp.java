package com.lincpay.chatbot.serviceimp;

import com.lincpay.chatbot.entities.ExpoToken;
import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.repository.ExpoRepo;
import com.lincpay.chatbot.serivce.ExpoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExpoServiceImp implements ExpoService {
    private static final Logger logger = LoggerFactory.getLogger(ExpoServiceImp.class);

   @Autowired
   ExpoRepo repo;

    @Override
    public ResponseEntity<ResponseModel> storeExpoToken(ExpoToken dto) {
        try {
            // ✅ Check if token already exists for this user
            Optional<ExpoToken> existingTokenOpt = repo.findByUserId(dto.getUserId());

            if (!existingTokenOpt.isPresent()) {
                // ✅ If not present, create new
                ExpoToken expoToken = new ExpoToken();
                expoToken.setExpoToken(dto.getExpoToken());
                expoToken.setUserId(dto.getUserId());
                repo.save(expoToken);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseModel<>("created", "token created", HttpStatus.CREATED.value()));
            } else {
                // ✅ If present, update token
                ExpoToken existingToken = existingTokenOpt.get();
                existingToken.setExpoToken(dto.getExpoToken());
                repo.save(existingToken);

                return ResponseEntity.ok(
                        new ResponseModel<>("updated", "token updated", HttpStatus.OK.value()));
            }
        } catch (Exception e) {
            logger.error("Expo Token Store Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>("error", "internal_server_error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

}
