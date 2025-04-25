package com.lincpay.chatbot.controller;

import java.math.BigDecimal;
import java.util.List;

import com.lincpay.chatbot.dto.Request.*;
import com.lincpay.chatbot.dto.response.WithdrawalResponseDto;
import com.lincpay.chatbot.util.AmountToWordsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.TelegramAdminGroup;
import com.lincpay.chatbot.serivce.TelegramGroupService;
import com.lincpay.chatbot.serviceimp.TelegramBotService;

@RestController
@CrossOrigin("*")
@RequestMapping("/notification")
public class TelegramNotificationController {
    private static final Logger logger = LoggerFactory.getLogger(TelegramNotificationController.class);

    private final TelegramBotService telegramBotService;

    public TelegramNotificationController(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @Autowired
    TelegramGroupService telegramGroupService;

    @Autowired
    AmountToWordsUtil amountToWordsUtil;

    @PostMapping("/telegramNotificationPayinLimitUpadte")
    public ResponseEntity<ResponseModel<Object>> showPayinSidLimitTelegramBot(
            @RequestBody List<GetPayinSidLimit> getPayinSidLimitList) {
        try {
            // ✅ Fetch all admin groups to send the Payin SID limit updates
            List<TelegramAdminGroup> telegramAdminGroups = telegramGroupService.getAllAdminGroupId();

            // ✅ Check if admin groups are found
            if (telegramAdminGroups.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel<>(
                                "Group not found.",
                                "not_found",
                                HttpStatus.NOT_FOUND.value(),
                                null
                        ));
            }

            // 🔄 Loop through all admin groups and send the Payin SID limit to each
            for (TelegramAdminGroup adminGroup : telegramAdminGroups) {
                String chatIdAdmin = adminGroup.getGroupChatId();

                for (GetPayinSidLimit getPayinSidLimit : getPayinSidLimitList) {
                    telegramBotService.sendMessage(chatIdAdmin, getPayinSidLimit.toString());
                }
            }

            // 🎉 Return success response
            return ResponseEntity.ok(
                    new ResponseModel<>(
                            "Message sent successfully.",
                            "success",
                            HttpStatus.OK.value(),
                            null
                    )
            );

        } catch (Exception e) {
            logger.error("Internal Server Error", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(
                            "Error occurred while sending messages.",
                            "internal_server_error",
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            null
                    ));
        }
    }

    @PostMapping("/telegramNotificationMerchantWithdrawalRequest")
    public ResponseEntity<ResponseModel<Object>> sendWithdrawalRequestMessage(
            @RequestBody WithdrawalRequestReceive withdrawalRequest) {
        try {
            // ✅ Fetch all admin groups to send the withdrawal request
            List<TelegramAdminGroup> telegramAdminGroups = telegramGroupService.getAllAdminGroupId();

            // 🔄 Loop through all admin groups and send the message
            for (TelegramAdminGroup adminGroup : telegramAdminGroups) {
                String chatIdAdmin = adminGroup.getGroupChatId();

                // ✅ Send withdrawal request with approve/reject buttons
                //telegramBotService.sendWithdrawalMessageWithButton(chatIdAdmin, withdrawalRequest);
                WithdrawalResponseDto withdrawalResponseDto = new WithdrawalResponseDto();
                withdrawalResponseDto.setWithdrawalId(withdrawalRequest.getWithdrawalId());
                withdrawalResponseDto.setMid(withdrawalRequest.getMid());
                withdrawalResponseDto.setAmount(withdrawalRequest.getAmount());
                withdrawalResponseDto.setCompanyName(withdrawalRequest.getCompanyName());
                withdrawalResponseDto.setVirtualAccountType(withdrawalRequest.getVirtualAccountType());
                withdrawalResponseDto.setCreateDateTime(withdrawalRequest.getCreateDateTime());
                withdrawalResponseDto.setAmountInWord(AmountToWordsUtil.convertToIndianCurrencyWords(withdrawalRequest.getAmount()));
                telegramBotService.sendMessage(chatIdAdmin, withdrawalResponseDto.toString());
                // 🔍 Check if the admin is authenticated (token exists for this chatId)
//                               String token = telegramBotService.getUserToken(chatIdAdmin);
//                if (token == null) {
//                    telegramBotService.sendMessage(chatIdAdmin, "⚠️ You are not logged in! Please use /start to login.");
//                }
            }

            // 🎉 Success response
            ResponseModel<Object> response = new ResponseModel<>(
                    "Withdrawal request sent successfully to all admin groups.",
                    "success",
                    HttpStatus.OK.value(),
                    null
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error occurred while sending withdrawal request: {}", e.getMessage(), e);

            ResponseModel<Object> errorResponse = new ResponseModel<>(
                    "Failed to send withdrawal request. Please try again later.",
                    "internal_server_error",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/telegramNotificationPayinLimitExceed")
    public ResponseEntity<ResponseModel<Object>> payinLimitExceed(@RequestBody PayinLimitExceedRequestDto payinLimitExceedRequestDto) {
        try {
            // ✅ Fetch all admin groups to send the Payin SID limit updates
            List<TelegramAdminGroup> telegramAdminGroups = telegramGroupService.getAllAdminGroupId();

            if (telegramAdminGroups.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel<>(
                                "Group not found.",
                                "not_found",
                                HttpStatus.NOT_FOUND.value(),
                                null
                        ));
            }

            // 🔄 Loop through all admin groups and send the Payin SID limit to each
            for (TelegramAdminGroup adminGroup : telegramAdminGroups) {
                String chatIdAdmin = adminGroup.getGroupChatId();
                telegramBotService.sendMessage(chatIdAdmin, payinLimitExceedRequestDto.toString());
            }

            return ResponseEntity.ok(
                    new ResponseModel<>(
                            "Message sent successfully.",
                            "success",
                            HttpStatus.OK.value(),
                            null
                    )
            );

        } catch (Exception e) {
            logger.error("Internal Server Error", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(
                            "Error occurred while sending messages.",
                            "internal_server_error",
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            null
                    ));
        }
    }

    @PostMapping("/payoutLowBalanceNotification")
    public ResponseEntity<ResponseModel<Object>> payoutLowBalanceNotification(@RequestBody PayoutInsufficientBalanceRequestDto payoutInsufficientBalanceRequestDto) {
        try {
            // ✅ Fetch all admin groups to send the Payin SID limit updates
            List<TelegramAdminGroup> telegramAdminGroups = telegramGroupService.getAllAdminGroupId();


            if (telegramAdminGroups.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel<>(
                                "Group not found.",
                                "not_found",
                                HttpStatus.NOT_FOUND.value(),
                                null
                        ));
            }

            for (TelegramAdminGroup adminGroup : telegramAdminGroups) {
                String chatIdAdmin = adminGroup.getGroupChatId();
                telegramBotService.sendMessage(chatIdAdmin, payoutInsufficientBalanceRequestDto.toString());
            }


            return ResponseEntity.ok(
                    new ResponseModel<>(
                            "Message sent successfully.",
                            "success",
                            HttpStatus.OK.value(),
                            null
                    )
            );

        } catch (Exception e) {
            logger.error("Internal Server Error", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(
                            "Error occurred while sending messages.",
                            "internal_server_error",
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            null
                    ));
        }
    }

//    @PostMapping("/failed10TxnContinue")
//    public ResponseEntity<String> failed10TxnContinue(@RequestBody Notification10FailedTxnRequestDto notification10FailedTxnRequestDto, @RequestHeader("Content-Type") String contentType) {
//        try {
//            // Fetch all admin groups
//            List<TelegramAdminGroup> telegramAdminGroups = telegramGroupService.getAllAdminGroupId();
//
//            // If no admin groups are found
//            if (telegramAdminGroups.isEmpty()) {
//                logger.warn("No admin groups found to send the alert for MID: {}", notification10FailedTxnRequestDto.getMid());
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No admin groups configured.");
//            }
//
//            // Send messages to all admin groups
//            for (TelegramAdminGroup adminGroup : telegramAdminGroups) {
//                String chatIdAdmin = adminGroup.getGroupChatId();
//                telegramBotService.sendMessage(chatIdAdmin, notification10FailedTxnRequestDto.toString());
//            }
//
//            // Log success and return response
//            logger.info("10 Failed Txn Alert sent for MID: {}", notification10FailedTxnRequestDto.getMid());
//            return ResponseEntity.ok("Alert sent successfully.");
//
//        } catch (Exception e) {
//            // Log error and return error response
//            logger.error("Failed to send 10 failed txn alert for MID: {}", notification10FailedTxnRequestDto.getMid(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error occurred.");
//        }
//    }


//    @PostMapping("/initiatedTxnAlert")
//    public ResponseEntity<String> initiatedTxnAlert(@RequestBody InitiatedTxnAlertRequestDto requestDto)
//    {
//
//        if (requestDto == null) {
//            logger.error("Received null requestDto.");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request body is missing or malformed.");
//        }
//        try {
//            List<TelegramAdminGroup> telegramAdminGroups = telegramGroupService.getAllAdminGroupId();
//
//            if (telegramAdminGroups.isEmpty()) {
//                logger.warn("No admin groups found to send the alert for MID: {}", requestDto.getMid());
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No admin groups configured.");
//            }
//
//            for (TelegramAdminGroup adminGroup : telegramAdminGroups) {
//                String chatIdAdmin = adminGroup.getGroupChatId();
//                telegramBotService.sendMessage(chatIdAdmin, requestDto.toString());
//            }
//
//            logger.info("initiated Txn Alert sent for MID: {}", requestDto.getMid());
//            return ResponseEntity.ok("Alert sent successfully.");
//        }
//        catch (Exception e)
//        {
//            logger.error("Failed to send initiated txn alert.",requestDto.toString(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error occurred.");
//        }
//    }
}
