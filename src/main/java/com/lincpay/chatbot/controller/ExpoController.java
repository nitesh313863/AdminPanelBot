package com.lincpay.chatbot.controller;

import com.lincpay.chatbot.entities.ExpoToken;
import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.TelegramMerchantGroup;
import com.lincpay.chatbot.entities.User;
import com.lincpay.chatbot.repository.ExpoRepo;
import com.lincpay.chatbot.repository.TelegramMerchantGroupRepo;
import com.lincpay.chatbot.repository.UserRepo;
import com.lincpay.chatbot.serivce.ExpoService;
import com.lincpay.chatbot.serviceimp.MerchantGroupChatAdminPanelServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/expo")
@CrossOrigin("*")
public class ExpoController {
    @Autowired
    ExpoService expoService;
    @Autowired
    ExpoRepo expoRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    TelegramMerchantGroupRepo telegramMerchantGroupRepo;
    private static final Logger logger = LoggerFactory.getLogger(ExpoController.class);


    @PostMapping("token-store")
    public ResponseEntity<ResponseModel> storeToken(@RequestBody ExpoToken dto) {
        return expoService.storeExpoToken(dto);
    }
    @GetMapping("/test")
    public ResponseEntity<?> test(@RequestParam String groupId) {
        try {
            // Validate input
            if (groupId == null || groupId.trim().isEmpty()) {
                logger.warn("Empty groupId provided in request");
                return ResponseEntity.badRequest().body("Group ID cannot be empty");
            }

            // Find the group
            TelegramMerchantGroup telegramMerchantGroup = telegramMerchantGroupRepo.findByGroupChatId(groupId);
            if (telegramMerchantGroup == null) {
                logger.warn("No group found with groupChatId: {}", groupId);
                return ResponseEntity.notFound().build();
            }

            // Get user IDs in the group
            List<Long> userIds = userRepo.findUserIdsByGroupId(telegramMerchantGroup.getId());
            if (userIds == null || userIds.isEmpty()) {
                logger.info("No users found in group with ID: {}", telegramMerchantGroup.getId());
                return ResponseEntity.ok().body("No users in this group");
            }

            // Process tokens
            List<String> allTokens = new ArrayList<>();
            for (Long userId : userIds) {
                try {
                    List<ExpoToken> tokens = expoRepo.findByUserIds(userId);
                    tokens.forEach(token -> {
                        logger.debug("Found token: {} for user: {}", token.getExpoToken(), userId);
                        allTokens.add(token.getExpoToken());
                    });
                } catch (Exception e) {
                    logger.error("Error fetching tokens for user ID: {}", userId, e);
                    // Continue processing other users even if one fails
                }
            }

            if (allTokens.isEmpty()) {
                logger.info("No tokens found for any users in group: {}", groupId);
                return ResponseEntity.ok().body("No tokens found for users in this group");
            }

            return ResponseEntity.ok().body(allTokens);

        } catch (Exception e) {
            logger.error("Unexpected error processing groupId: {}", groupId, e);
            return ResponseEntity.internalServerError().body("An error occurred while processing your request");
        }
    }


}
