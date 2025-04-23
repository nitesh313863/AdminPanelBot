package com.lincpay.chatbot.controller;

import com.lincpay.chatbot.constant.ApplicationConstant;
import com.lincpay.chatbot.dto.response.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lincpay.chatbot.dto.Request.SendAlertMeassegeInMidGroup;
import com.lincpay.chatbot.dto.Request.SendBroadCastGroupRequest;
import com.lincpay.chatbot.entities.TelegramMerchantGroup;
import com.lincpay.chatbot.serivce.TelegramGroupService;
import com.lincpay.chatbot.serviceimp.TelegramBotService;

@RestController
@CrossOrigin("*")
@RequestMapping("/send-message")
public class SendMessageController {
	private static final Logger logger = LoggerFactory.getLogger(SendMessageController.class);

    private final TelegramBotService telegramBotService;
    
    public SendMessageController(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }
    @Autowired
    TelegramGroupService telegramGroupService;

	@PostMapping("/sendMessageAllSelectedGroup")
	public ResponseEntity<ResponseModel<Object>> sendBroadCastMessage(@RequestBody SendBroadCastGroupRequest request) {


			if (request.getGroupChatIds() == null || request.getGroupChatIds().isEmpty()) {
				ResponseModel<Object> response = new ResponseModel<>(
						"No groups selected.",
						"bad request",
						HttpStatus.BAD_REQUEST.value(),
						null
				);
				return ResponseEntity.badRequest().body(response);
			}

			if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
				ResponseModel<Object> response = new ResponseModel<>(
						"Message cannot be empty.",
						"bad request",
						HttpStatus.BAD_REQUEST.value(),
						null
				);
				return ResponseEntity.badRequest().body(response);
			}

			// ✅ Send the broadcast message to selected groups
			for (String groupChatId : request.getGroupChatIds()) {
				if(groupChatId.startsWith("-"))
				{
					telegramBotService.sendMessage(groupChatId, request.getMessage());
				}
				else{
					ResponseModel<Object> response = new ResponseModel<>(
							"Wrong Group Selected",
							"bad request",
							HttpStatus.BAD_REQUEST.value(),
							null
					);
					return ResponseEntity.badRequest().body(response);
				}


			}

			ResponseModel<Object> response = new ResponseModel<>(
					"Broadcast sent successfully!",
					"success",
					HttpStatus.OK.value(),
					null
			);

			return ResponseEntity.ok(response);


	}

	@PostMapping("/sendAlertMessageInMerchantGroup")
	public ResponseEntity<ResponseModel<Object>> sendAlertMessageInMerchantGroup(
			@RequestBody SendAlertMeassegeInMidGroup sendAlertMeassegeInMidGroup) {
		try {
			// ✅ Fetch merchant group by MID
			TelegramMerchantGroup telegramMerchantGroup = telegramGroupService.getMerchantGroupByMid(sendAlertMeassegeInMidGroup);

			// ✅ Check if group is found
			if (telegramMerchantGroup != null && telegramMerchantGroup.getGroupChatId() != null) {
				// ✅ Send message to the correct group
				telegramBotService.sendMessage(telegramMerchantGroup.getGroupChatId(), sendAlertMeassegeInMidGroup.toString());

				ResponseModel<Object> response = new ResponseModel<>(
						"Alert message sent successfully to group: " + telegramMerchantGroup.getGroupName(),
						"success",
						HttpStatus.OK.value(),
						null
				);

				return ResponseEntity.ok(response);
			} else {
				// ❌ Group not found
				ResponseModel<Object> response = new ResponseModel<>(
						"No group found for MID: " + sendAlertMeassegeInMidGroup.getMid(),
						"not found",
						HttpStatus.NOT_FOUND.value(),
						null
				);

				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
		} catch (Exception e) {
			// ❌ Error handling
			ResponseModel<Object> response = new ResponseModel<>(
					"Error while sending the alert message. Please try again.",
					"error",
					HttpStatus.INTERNAL_SERVER_ERROR.value(),
					null
			);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}

