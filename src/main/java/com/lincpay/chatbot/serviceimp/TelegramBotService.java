package com.lincpay.chatbot.serviceimp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lincpay.chatbot.dto.Request.WithdrawalRequestReceive;
import com.lincpay.chatbot.dto.response.FetchMidDataResponse;
import com.lincpay.chatbot.dto.response.PayinSidLimtReciveRespose;
import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.MerchantChat;
import com.lincpay.chatbot.entities.TelegramAdminGroup;
import com.lincpay.chatbot.entities.TelegramMerchantGroup;
import com.lincpay.chatbot.entities.WithdrawalRequestAction;
import com.lincpay.chatbot.repository.MerchantChatRepo;
import com.lincpay.chatbot.repository.WithdrawalRequestActionRepo;
import com.lincpay.chatbot.serivce.TelegramGroupService;
import com.lincpay.chatbot.serivce.UserValidateService;
import com.lincpay.chatbot.util.MultipartFileResource;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllGroupChats;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@EnableCaching
public class TelegramBotService extends TelegramLongPollingBot {
	@Value("${telegram.bot.token}")
	private String botToken;

	@Value("${telegram.bot.username}")
	private String botUsername;


	@Autowired
	WebSocketSenderService webSocketSenderService;


	@Autowired
	private TelegramGroupService telegramGroupService;

	@Autowired
	private AddMenuServiceImp addMenuServiceImp;

	@Autowired
	private UserValidateService userValidateService;

	@Autowired
	private ApiCallerService apiCallerService;

	@Autowired
	WithdrawalRequestActionRepo withdrawalRequestActionRepo;

	@Autowired
	MerchantChatRepo merchantChatRepo;


	@Autowired
	GroupMemberServiceImp groupMemberServiceImp;

	@Value("${telegram.document.upload}")
	private String uploadDirectory1;


	private static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);

	// Maps to store user states and data
	private final Map<String, Boolean> awaitingBoardCastMessage = new HashMap<>();
	private final Map<String, Boolean> awaitingEmail = new HashMap<>();
	private final Map<String, String> userTokens = new HashMap<>();
	private final Map<String, String> awaitingRemarkReject = new HashMap<>();
	private final Map<String, String> awaitingTransactionDate = new HashMap<>();
	private final Map<String, String> awaitingUtr = new HashMap<>();
	private final Map<String, String> awaitingWithdrawalId = new HashMap<>();
	private final Map<String, Integer> withdrawalMessageIds = new HashMap<>();
	private Map<String, Boolean> awaitingPassphrase = new HashMap<>();
	private Map<String, String> userEmails = new HashMap<>();
	private Map<String, String> userPassphrases = new HashMap<>();
	private final List<BotCommand> botCommands = new ArrayList<>();
	private Set<String> adminGroupIds = new HashSet<>();
	private Set<String> merchantGroupIds = new HashSet<>();
	private Map<String, String> midBusinessNameMap = new HashMap<>();

//    private static final String PYTHON_EXECUTABLE = "python"; // Use 'python' for Windows
//    private static final String API_ID = "26395329"; // Get this from my.telegram.org
//    private static final String API_HASH = "780f9dd42272080669cd02ad12769e09"; // Get this from my.telegram.org
//    private static final String PHONE_NUMBER = "+917024515807"; // Example: +1234567890


	@Override
	public String getBotUsername() {
	    return botUsername;
	}

	@Override
	public String getBotToken() {
	    return botToken;
	}

	// Process incoming updates asynchronously and handle message or callback queries
	@Override
	@Async
	public void onUpdateReceived(Update update) {
		System.out.println("onUpdateReceived: " + update);
		try {
			if (update.hasCallbackQuery()) {
				logger.info("Processing callback query...");
				handleCallbackQuery(update.getCallbackQuery());
			} else if (update.hasMessage()) {
				if (update.getMessage().hasText()) {
					logger.info("Processing incoming text message...");
					handleMessage(update.getMessage());
				} else if (update.getMessage().hasPhoto()) {
					logger.info("Processing incoming photo...");
					handleMessage(update.getMessage());
				} else if (update.getMessage().hasDocument()) {
					logger.info("Processing incoming document...");
					handleMessage(update.getMessage());
				}
			}
		} catch (Exception e) {
			logger.error("Error processing update", e);
		}
	}


	public String getFilePath(String fileId, String botToken) {
		String url = "https://api.telegram.org/bot" + botToken + "/getFile?file_id=" + fileId;
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		JSONObject jsonObject = new JSONObject(response.getBody());
		return jsonObject.getJSONObject("result").getString("file_path");
	}
	public byte[] downloadTelegramFile(String filePath, String botToken) throws IOException {
		String fileUrl = "https://api.telegram.org/file/bot" + botToken + "/" + filePath;
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(fileUrl, byte[].class);
	}
	@Transactional
	public void storeTelegramPhoto(Message message) {
		String botT = botToken;

		try {
			// Get the list of photos and pick the largest one (highest resolution)
			List<PhotoSize> photos = message.getPhoto();
			if (photos == null || photos.isEmpty()) {
				// Handle case when no photos are attached
				logger.warn("No photos found in the message");
				return;
			}
			PhotoSize largestPhoto = photos.get(photos.size() - 1); // Highest resolution photo
			String fileId = largestPhoto.getFileId();

			// Get the file path for the photo
			String filePath = getFilePath(fileId, botT);
			byte[] photoBytes = downloadTelegramFile(filePath, botT);

			// Create a new MerchantChat entity and set its properties
			MerchantChat chat = new MerchantChat();
			chat.setChatId(String.valueOf(message.getChatId()));
			chat.setUserName(message.getFrom().getUserName());
			chat.setUserId(message.getFrom().getId());
			chat.setMsgId(message.getMessageId());
			chat.setMessageDate(LocalDateTime.now());

			// Set caption if available
			if (message.getCaption() != null) {
				chat.setCaption(message.getCaption());
			}

			// Store the photo (as byte array) and related file information
			chat.setPhoto(photoBytes);                      // Byte array for photo
			chat.setPhotoFileId(fileId);                    // Store the file_id for later reference
			chat.setFileType("image/jpeg");                 // Optional: could detect dynamically
			chat.setMsgText(null);                          // No text in this case (since it's an image)

			// Save the chat entity to the database
			webSocketSenderService.sendMerchantGroupMessage(chat);
			merchantChatRepo.save(chat);

		} catch (Exception e) {
			// Log the exception for better debugging
			e.printStackTrace();
			System.err.println("Error while storing telegram photo: " + e.getMessage());
		}
	}


	// Handles incoming messages and identifies the type of chat
	private void handleMessage(Message message) {
	    String chatId = String.valueOf(message.getChatId());
	    String receivedText = message.getText();
	    User user = message.getFrom();
	    String userName = user.getFirstName() + " " + (user.getLastName() != null ? user.getLastName() : "");
	    Long userId = user.getId();
	    Integer msgId = message.getMessageId();

	    logger.info("Received message: '{}' from user: '{}' in chat: '{}'", receivedText, userName, chatId);


        // If adminGroupIds doesn't contain the chatId, refresh it from DB
        if (!adminGroupIds.contains(chatId)) {
            List<TelegramAdminGroup> telegramAdminGroups = telegramGroupService.getAllAdminGroupId();
            for (TelegramAdminGroup adminGroup : telegramAdminGroups) {
                adminGroupIds.add(adminGroup.getGroupChatId());
            }
        }
	    // Check if the message is from Admin Group, Merchant Group, or Private Chat
	    if (adminGroupIds.contains(chatId)) {
//	        logger.info("Message received in Admin Group. Processing...");
	        handleAdminGroupMessage(chatId, receivedText, userName, userId, user, msgId);
	    }
// Refresh merchant group IDs if not loaded yet
		if (!merchantGroupIds.contains(chatId)) {
			List<TelegramMerchantGroup> telegramMerchantGroups = telegramGroupService.getAllMerchantGroups();
			for (TelegramMerchantGroup merchantGroup : telegramMerchantGroups) {
				merchantGroupIds.add(merchantGroup.getGroupChatId());
			}
		}

// Now check AFTER refreshing
		if (adminGroupIds.contains(chatId)) {
			handleAdminGroupMessage(chatId, receivedText, userName, userId, user, msgId);
		} else if (merchantGroupIds.contains(chatId)) {
			logger.warn("Message received in Merchant Group. Access denied.");
			handleMerchantChat(chatId,userId,userName,msgId,receivedText,message);
		} else if (message.getChat().isUserChat()) {
			handlePrivateChatMessage(chatId, receivedText, userName, userId, user, msgId);
		} else {
			logger.warn("Message received in an unknown or unregistered chat. Access denied.");
			sendMessage(chatId, "❌ Access denied. You don't have the necessary permissions. Chat ID: " + chatId);
		}

	}

	private void handleMerchantChat(String chatId, Long userId, String userName, Integer msgId, String text_msg, Message message) {
			// Handle Text Message
			if (text_msg != null && !text_msg.isEmpty()) {
				if (text_msg.startsWith("/")) {
					sendMessage(chatId, "Message received in Merchant Group. ❌  Access denied.");
				}
				else {
					MerchantChat merchantChat = new MerchantChat();
					merchantChat.setMsgText(text_msg);
					merchantChat.setMsgId(msgId);
					merchantChat.setUserId(userId);
					merchantChat.setChatId(chatId);
					merchantChat.setUserName(userName);
					merchantChat.setMessageDate(LocalDateTime.now());  // Set current timestamp
					merchantChatRepo.save(merchantChat);

					// Send via WebSocket
					webSocketSenderService.sendMerchantGroupMessage(merchantChat);
				}

			}

			// Handle Photo Message
			else if (message.hasPhoto()) {
				storeTelegramPhoto(message);
			}

//			 Handle Document Message (PDF)
			else if (message.hasDocument()) {
				storeTelegramPdf(message);
			}


	}
	private void storeTelegramPdf(Message message) {
		Document document = message.getDocument();
		String fileId = document.getFileId();
		String fileName = document.getFileName();
		String mimeType = document.getMimeType();
		Integer fileSize= document.getFileSize();

		if (fileName != null && mimeType != null && mimeType.startsWith("application/")) {
			MerchantChat merchantChat = new MerchantChat();
			merchantChat.setDocFileId(fileId);
			merchantChat.setFileName(fileName);
			merchantChat.setFileType(mimeType);
			merchantChat.setMsgId(message.getMessageId());
			merchantChat.setUserId(message.getFrom().getId());
			merchantChat.setChatId(String.valueOf(message.getChatId()));
			merchantChat.setUserName(message.getFrom().getUserName());
			merchantChat.setMessageDate(LocalDateTime.now());
			merchantChat.setFileSize(fileSize);

			// Step 1: Get the file path from Telegram
			String filePath = getFilePathFromTelegram(fileId);
			if (filePath != null) {
				String baseDirectory = uploadDirectory1;
				String actualFileName = filePath.substring(filePath.lastIndexOf("/") + 1);
				Path resolvedPath = Paths.get(baseDirectory, actualFileName).normalize();

				// Step 2: Download file
				byte[] fileData = downloadFileDataFromTelegram(filePath);
				merchantChat.setFileData(fileData);
				merchantChat.setDocFilePath(resolvedPath.toString());

				// ✅ Step 3: Write file to disk
				try {
					Files.createDirectories(resolvedPath.getParent()); // Ensure directory exists
					Files.write(resolvedPath, fileData);
					System.out.println("✅ File written to disk at: " + resolvedPath);
				} catch (IOException e) {
					System.err.println("❌ Failed to write file to disk: " + e.getMessage());
					e.printStackTrace();
				}
			}

			// Step 4: Save to DB

			merchantChatRepo.save(merchantChat);
			// Send via WebSocket
			webSocketSenderService.sendMerchantGroupMessage(merchantChat);
		}
	}



	private String getFilePathFromTelegram(String fileId) {
		String url = "https://api.telegram.org/bot" + botToken + "/getFile?file_id=" + fileId;
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				return extractFilePathFromResponse(response.getBody());
			} else {
				logger.warn("Telegram API request failed with status: " + response.getStatusCode());
				return null;
			}
		} catch (HttpClientErrorException e) {
			logger.warn("Telegram returned error: " + e.getResponseBodyAsString(), e);
			return null;
		}
	}

	private String extractFilePathFromResponse(String responseBody) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(responseBody);
			if (root.path("ok").asBoolean(false)) {
				return root.path("result").path("file_path").asText(null);
			}
		} catch (Exception e) {
			logger.error("Error extracting file path from response: " + responseBody, e);
		}
		return null;
	}

	private byte[] downloadFileDataFromTelegram(String filePath) {
		String fileUrl = "https://api.telegram.org/file/bot" + botToken + "/" + filePath;
		RestTemplate restTemplate = new RestTemplate();
		try {
			return restTemplate.getForObject(fileUrl, byte[].class);
		} catch (HttpClientErrorException e) {
			logger.warn("Failed to download file: " + e.getResponseBodyAsString(), e);
			return null;
		}
	}

	// Handles callback queries received from button interactions
	private void handleCallbackQuery(CallbackQuery callbackQuery) {
	    String chatId = callbackQuery.getMessage().getChatId().toString();
	    String data = callbackQuery.getData();
	    Long userId = callbackQuery.getFrom().getId();
	    Chat chat = callbackQuery.getMessage().getChat(); // Get chat details

//	    logger.info("Callback query received from user ID: {} with data: {}", userId, data);

	    // ✅ Check if the message is from a group
	    if (chat.isGroupChat() || chat.isSuperGroupChat()) {
	        // ✅ Check if the user is an authorized admin (Only in group chats)
	        boolean checkPrivilege = groupMemberServiceImp.getGroupMemberByGroupIdAndUserId(chatId, userId);
	        if (!checkPrivilege) {
	            logger.warn("Unauthorized user with chatId: {}", chatId);
	            sendMessage(chatId, "❌ You are not authorized to perform this action.");
	            return;
	        }
			if(!userTokens.containsKey(chatId)) {
				sendMessage(chatId, "⚠️ You are not logged in! Please use /start to login.");
				return;
			}
	    }

	    // ✅ Handle approval action
	    if (data.startsWith("approve_")) {
	        String withdrawalId = data.replace("approve_", "");

	        if (withdrawalId.isEmpty()) {
	            sendMessage(chatId, "⚠️ Invalid withdrawal ID provided.");
	            return;
	        }

	        awaitingWithdrawalId.put(chatId, withdrawalId);
//	        logger.info("Approval request received for withdrawal ID: {}", withdrawalId);

	        sendMessage(chatId, "✅ Withdrawal ID: *" + withdrawalId + "*\nPlease enter the *Transaction Date* (YYYY-MM-DD):");
	        awaitingTransactionDate.put(chatId, "waiting");

	    // ✅ Handle rejection action
	    } else if (data.startsWith("reject_")) {
	        String withdrawalId = data.replace("reject_", "");

	        if (withdrawalId.isEmpty()) {
	            sendMessage(chatId, "⚠️ Invalid withdrawal ID provided.");
	            return;
	        }

	        awaitingRemarkReject.put(chatId, withdrawalId);
	        logger.info("Rejection request received for withdrawal ID: {}", withdrawalId);

	        sendMessage(chatId, "❌ Withdrawal ID: *" + withdrawalId + "*\nPlease enter the reason for rejection:");

	    // ✅ Handle payout balance request for selected MID
	    } else if (data.startsWith("payoutBalance")) {
	        String selectedMid = data.replace("payoutBalance", "");

	        if (selectedMid.isEmpty()) {
	            sendMessage(chatId, "⚠️ Invalid MID provided for payout balance.");
	            return;
	        }

	        logger.info("Payout balance request for MID: {}", selectedMid);
	        sendMessage(chatId, "✅ You selected MID: *" + selectedMid + "* to check the payout balance.");
	        handlePayoutBalanceCheck(chatId, userTokens.get(chatId), selectedMid);

	    // ✅ Handle pay-in limit for selected MID
	    } else if (data.startsWith("payin_limit")) {
	        String selectedMid = data.replace("payin_limit", "");

	        if (selectedMid.isEmpty()) {
	            sendMessage(chatId, "⚠️ Invalid MID provided for pay-in limit.");
	            return;
	        }

	        logger.info("Pay-in limit request for MID: {}", selectedMid);
	        sendMessage(chatId, "✅ You selected MID: *" + selectedMid + "* to check the pay-in limit.");
	        handlePayinLimtSid(chatId, userTokens.get(chatId), selectedMid);

	    // ❗️ Handle unknown or unexpected callback data
	    } else {
	        logger.warn("Unknown callback data received: {}", data);
	        sendMessage(chatId, "⚠️ Unknown action. Please try again or contact support.");
	    }
	}

	// Handles Payin Limit by MID and sends response to the user
	private void handlePayinLimtSid(String chatId, String token, String mid) {
	    logger.info("Requesting payin limit for MID: {} with token: {}", mid, token);
	    ResponseModel<List<PayinSidLimtReciveRespose>> responseModel = apiCallerService.payinLimtSid(mid, token);

	    // Check if response is null or invalid
	    if (responseModel == null) {
	        logger.error("Null response received for payin limit request.");
	        sendMessage(chatId, "⚠️ Internal Server Error. Please try again later.");
	        return;
	    }

	    // Process response based on status code
	    switch (responseModel.getStatusCode()) {
	        case 200: // Success
	            if (responseModel.getData() != null) {
	                List<PayinSidLimtReciveRespose> payinLimtSidList = (List<PayinSidLimtReciveRespose>) responseModel.getData();
	                if (payinLimtSidList.isEmpty()) {
	                    logger.info("No Payin limits found for MID: {}", mid);
	                    sendMessage(chatId, "❗ No Payin limits found for this MID.");
	                } else {
	                    logger.info("Sending payin limit details for MID: {}", mid);
	                    sendFormattedPayinLimits(chatId, payinLimtSidList);
	                }
	            } else {
	                logger.warn("No data available for payin limit request.");
	                sendMessage(chatId, "ℹ️ No data available.");
	            }
	            break;

	        case 401: // Token expired
	            logger.warn("Token expired for payin limit request. MID: {}", mid);
	            sendMessage(chatId, "🔐 Your token has expired. Please enter /start to regenerate it.");
	            break;

	        case 404: // Data not found
	            logger.warn("No data found for MID: {}", mid);
	            sendMessage(chatId, "❗ No data found for MID: `" + mid + "`.");
	            break;

	        case 500: // Internal Server Error
	            logger.error("Internal server error during payin limit request.");
	            sendMessage(chatId, "⚠️ Something went wrong. Please try again later.");
	            break;

	        default:
	            logger.error("Unexpected error during payin limit request.");
	            sendMessage(chatId, "❗ An unexpected error occurred. Please contact support.");
	            break;
	    }
	}

	// ✅ Method to format and send the Payin Limits data nicely
	private void sendFormattedPayinLimits(String chatId, List<PayinSidLimtReciveRespose> payinLimtSidList) {
	    StringBuilder responseMessage = new StringBuilder("✅ *Payin Limits for MID:*\n\n");

	    // Configure currency format for INR
	    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

	    // Loop through the list of Payin Limits and format the response
	    for (PayinSidLimtReciveRespose payinLimtSid : payinLimtSidList) {
	        try {
	            // Format maxTxnAmount and remainingLimit properly
	            String maxTxnAmountFormatted = currencyFormat.format(Double.parseDouble(payinLimtSid.getMaxTxnAmount()));
	            String remainingLimitFormatted = currencyFormat.format(Double.parseDouble(payinLimtSid.getRemainingLimit()));

	            // Append all Payin Limit details for each SID
	            responseMessage
	                    .append("👉 *SID:* ").append(payinLimtSid.getSid()).append("\n")
	                    .append("🏢 *Company:* ").append(payinLimtSid.getCompany()).append("\n")
	                    .append("💰 *Max Transaction Amount:* ").append(maxTxnAmountFormatted).append("\n")
	                    .append("📉 *Remaining Limit:* ").append(remainingLimitFormatted).append("\n")
	                    .append("🌐 *Domain:* ").append(payinLimtSid.getDomain()).append("\n")
	                    .append("⚡️ *Status:* ").append(payinLimtSid.isDeleted() ? "❌ Inactive" : "✅ Active").append("\n")
	                    .append("🚗 *Vehicle Name:* ").append(payinLimtSid.getVehicleName() != null ? payinLimtSid.getVehicleName() : "N/A").append("\n")
	                    .append("🏷️ *Prefix:* ").append(payinLimtSid.getPrifix() != null ? payinLimtSid.getPrifix() : "N/A").append("\n")
	                    .append("🏦 *Merchant Name:* ").append(payinLimtSid.getMasterMarchantName() != null ? payinLimtSid.getMasterMarchantName() : "N/A").append("\n")
	                    .append("🔢 *Merchant VPA:* ").append(payinLimtSid.getMerchantVpa() != null ? payinLimtSid.getMerchantVpa() : "N/A").append("\n")
	                    .append("📅 *Created On:* ").append(payinLimtSid.getCreateDateTime()).append("\n")
	                    .append("🕒 *Updated On:* ").append(payinLimtSid.getUpdatedDateTime()).append("\n")
	                    .append("-----------------------------\n");
	        } catch (NumberFormatException e) {
	            logger.error("Error parsing transaction amount for SID: {}", payinLimtSid.getSid(), e);
	            responseMessage.append("⚠️ Error in amount format for SID: ").append(payinLimtSid.getSid()).append("\n");
	        }
	    }

	    // Send the formatted response
	    if (responseMessage.toString().isEmpty()) {
	        logger.info("No Payin limits found for chatId: {}", chatId);
	        sendMessage(chatId, "No Payin limits found!");
	    } else {
	        sendMessage(chatId, responseMessage.toString());
	    }
	}

	// ✅ Method to handle Payout Balance Check
	private void handlePayoutBalanceCheck(String chatId, String token, String mid) {
	    logger.info("Checking payout balance for MID: {}", mid);
	    Object payoutBalanceResponse = apiCallerService.callCombinedApi(mid, token, midBusinessNameMap.get(mid));

	    if (payoutBalanceResponse == null) {
	        logger.warn("No response from API for MID: {}", mid);
	        sendMessage(chatId, "⚠️ No response from server. Please try again.");
	    } else if ("401".equals(payoutBalanceResponse.toString())) {
	        logger.warn("Token expired for chatId: {} and MID: {}", chatId, mid);
	        sendMessage(chatId, "Your Token Is Expired. Please Enter /start Command.");
	    } else if ("304".equals(payoutBalanceResponse.toString())) {
	        logger.info("Data not found for MID: {}", mid);
	        sendMessage(chatId, "Data Not Found");
	    } else {
	        logger.info("Payout balance retrieved successfully for MID: {}", mid);
	        sendMessage(chatId, payoutBalanceResponse.toString());
	    }
	}

	// ✅ Handle Admin Group Messages
	private void handleAdminGroupMessage(String chatId, String receivedText, String userName, Long userId, User user, Integer msgId) {
	    logger.info("Admin group message received from user: {} with chatId: {}", userName, chatId);
	    processCommonMessage(chatId, receivedText, userName, userId, user, msgId);
	}

	// ✅ Handle Private Chat Messages
	private void handlePrivateChatMessage(String chatId, String receivedText, String userName, Long userId, User user, Integer msgId) {
	    logger.info("Private chat message received from user: {} with chatId: {}", userName, chatId);
	    processCommonMessage(chatId, receivedText, userName, userId, user, msgId);
	}

	// ✅ Common method to handle Admin and Private Chat messages
	private void processCommonMessage(String chatId, String receivedText, String userName, Long userId, User user, Integer msgId) {
	    logger.debug("Processing message for user: {} with chatId: {}", userName, chatId);

	    // ✅ Handle Transaction Date Input
	    if (awaitingTransactionDate.containsKey(chatId) && "waiting".equals(awaitingTransactionDate.get(chatId))) {
	        awaitingTransactionDate.put(chatId, receivedText);
	        awaitingUtr.put(chatId, "waiting");
	        sendMessage(chatId, "✅ Now enter the *UTR* (Unique Transaction Reference):");
	        return;
	    }

	    // ✅ Handle UTR Input
	    if (awaitingUtr.containsKey(chatId) && "waiting".equals(awaitingUtr.get(chatId))) {
	        if (receivedText.length() >= 12) {
	            awaitingUtr.put(chatId, receivedText);
	            processApproval(chatId, userName, userId);
	        } else {
	            sendMessage(chatId, "❌ Please enter a valid 12-digit UTR:");
	            awaitingUtr.put(chatId, "waiting");
	        }
	        return;
	    }

	    // ✅ Handle Rejection Remarks
	    if (awaitingRemarkReject.containsKey(chatId)) {
	        processRejection(chatId, receivedText, userName, userId);
	        return;
	    }

	    // ✅ Handle Email Input for Login
	    if (awaitingEmail.getOrDefault(chatId, false)) {
	        userEmails.put(chatId, receivedText);
	        awaitingEmail.put(chatId, false);
	        awaitingPassphrase.put(chatId, true);
	        deleteMessage(chatId, msgId);
	        sendMessage(chatId, "🔑 Now, please enter your *Passphrase*:");
	        return;
	    }

	    // ✅ Handle Passphrase Input for Login
	    if (awaitingPassphrase.getOrDefault(chatId, false)) {
	        userPassphrases.put(chatId, receivedText);
	        awaitingPassphrase.put(chatId, false);
	        deleteMessage(chatId, msgId);
	        loginByEmailAndPassPhase(chatId, userEmails.get(chatId), userPassphrases.get(chatId), user.getFirstName(), user.getLastName());
	        return;
	    }

	    // ✅ Handle Broadcast Message
	    if (awaitingBoardCastMessage.containsKey(chatId) && awaitingBoardCastMessage.get(chatId)) {
	        broadCastMessage(chatId, receivedText);
	        return;
	    }

	    // ✅ Handle Start Command
	    if (receivedText.equalsIgnoreCase("/start") || receivedText.equalsIgnoreCase("/start@LincpayAdminPanelBot")) {
	        handleStartCommand(chatId, user.getFirstName());
	        return;
	    }

	    // ✅ Default: Handle Menu Selection
	    receivedText = receivedText.replaceAll("@LincpayAdminPanelBot", "").trim();
	    handleMenuSelection(chatId, receivedText, userTokens.get(chatId), user.getFirstName(), user.getLastName());
	}

	// ✅ Method to process Approval
	private void processApproval(String chatId, String userName, Long userId) {
	    try {
	        logger.info("Processing approval for chatId: {} by user: {}", chatId, userName);

	        // Retrieve stored data
	        String transactionDate = awaitingTransactionDate.get(chatId);
	        String utr = awaitingUtr.get(chatId);
	        String withdrawalId = awaitingWithdrawalId.get(chatId);
	        String token = userTokens.get(chatId);

	        // Check if all required data is available
	        if (transactionDate != null && utr != null && withdrawalId != null) {
	            ResponseModel response = apiCallerService.withdrawalRequestAction(
	                    token, "", "success", transactionDate, utr, Integer.parseInt(withdrawalId));

	            if (response != null && "Success".equalsIgnoreCase(response.getStatus())) {
	                sendMessage(chatId, "✅ Withdrawal Request Approved");

	                // ✅ Save approval to Database
	                WithdrawalRequestAction approval = new WithdrawalRequestAction();
	                approval.setWithdrawalId(Integer.parseInt(withdrawalId));
	                approval.setApprovedBy(userName);
	                approval.setApprovedById(userId);
	                approval.setStatus("approved");
	                approval.setTransactionDate(transactionDate);
	                approval.setUtr(utr);
	                withdrawalRequestActionRepo.save(approval);

	                logger.info("Approval saved successfully for withdrawalId: {}", withdrawalId);
	                markAsProcessed(chatId, withdrawalId, true, userName);
	                clearApprovalData(chatId);
	            } else {
	                logger.warn("Withdrawal Approval Failed: {}", response != null ? response.getMessage() : "Unknown error");
	                sendMessage(chatId, "❌ Withdrawal Approval Failed: " + (response != null ? response.getMessage() : "Unknown error"));
	            }
	        } else {
	            logger.error("Missing required data for approval process in chatId: {}", chatId);
	            sendMessage(chatId, "❌ Missing data. Please try again.");
	        }
	    } catch (Exception e) {
	        logger.error("Error occurred while approving withdrawal", e);
	        sendMessage(chatId, "❌ An error occurred while approving the withdrawal.");
	    }
	}


	// ✅ Method to clear stored approval data after processing
	private void clearApprovalData(String chatId) {
	    awaitingTransactionDate.remove(chatId);
	    awaitingUtr.remove(chatId);
	    awaitingWithdrawalId.remove(chatId);
		awaitingRemarkReject.remove(chatId);
	    logger.info("Approval data cleared for chatId: {}", chatId);
	}



	 //✅ Process Withdrawal Rejection with Remark
	private void processRejection(String chatId, String remark, String userName, Long userId) {
	    try {
	        String withdrawalId = awaitingRemarkReject.get(chatId);
	        String token = userTokens.get(chatId);

	        // ✅ Validate withdrawalId and token before proceeding
	        if (withdrawalId != null && token != null) {
	            logger.info("Processing withdrawal rejection for withdrawalId: {}, by user: {}", withdrawalId, userName);

	            // ✅ Call API to reject the withdrawal request
	            ResponseModel response = apiCallerService.withdrawalRequestAction(
	                token, remark, "reject", "", "", Integer.parseInt(withdrawalId)
	            );

	            // ✅ Handle successful rejection
	            if (response != null && "Success".equalsIgnoreCase(response.getStatus())) {
	                sendMessage(chatId, "❌ Withdrawal Request Rejected");

	                // ✅ Save rejection details to the database
	                WithdrawalRequestAction rejection = new WithdrawalRequestAction();
	                rejection.setWithdrawalId(Integer.parseInt(withdrawalId));
	                rejection.setRejectedBy(userName);
	                rejection.setRejectedById(userId);
	                rejection.setStatus("rejected");
	                rejection.setRemark(remark);
	                withdrawalRequestActionRepo.save(rejection);

	                // ✅ Mark the request as processed and clean up rejection data
	                markAsProcessed(chatId, withdrawalId, false, userName);
					clearApprovalData(chatId);
	                logger.info("Withdrawal request rejected successfully for withdrawalId: {}", withdrawalId);
	            } else {
	                sendMessage(chatId, "❌ Withdrawal Rejection Failed: " + response.getMessage());
	                logger.warn("Withdrawal rejection failed for withdrawalId: {}, message: {}", withdrawalId, response.getMessage());
	            }
	        } else {
	            sendMessage(chatId, "❌ Missing data. Please try again.");
	            logger.warn("Missing data for rejection: withdrawalId or token not found.");
	        }
	    } catch (Exception e) {
	        logger.error("Error rejecting withdrawal for chatId: {}", chatId, e);
	        sendMessage(chatId, "❌ An error occurred while rejecting the withdrawal.");
	    }
	}


	// ✅ Handle Group Messages and Store in Database
	private void handleGroupMessage(String chatId, String receivedText, String firstName, String lastName) {
	    try {
	        logger.info("Handling group message from user: {} in chatId: {}", firstName, chatId);
	        telegramGroupService.messageStoreInDb(chatId, receivedText, firstName, lastName);
	    } catch (Exception e) {
	        logger.error("Error handling group message for chatId: {}", chatId, e);
	        sendMessage(chatId, "An error occurred while processing your message.");
	    }
	}

	// ✅ Broadcast Message to All Groups
	private void broadCastMessage(String chatId, String receivedText) {
	    try {
	        logger.info("Broadcasting message initiated by chatId: {}", chatId);
	        List<TelegramMerchantGroup> telegramGroups = telegramGroupService.getAllGroupForBarodCast();

	        // ✅ Check if user wants to cancel broadcast
	        if ("exit".equalsIgnoreCase(receivedText)) {
	            awaitingBoardCastMessage.remove(chatId);
	            sendMessage(chatId, "Broadcast canceled.");
	            logger.info("Broadcast canceled by chatId: {}", chatId);
	        } else {
	            // ✅ Send broadcast message to all groups
	            for (TelegramMerchantGroup telegramGroup : telegramGroups) {
					logger.info(telegramGroup.getGroupChatId());
					sendMessage(telegramGroup.getGroupChatId(), receivedText);
	            }
	            awaitingBoardCastMessage.remove(chatId);
	            sendMessage(chatId, "Message broadcast successfully!");
	            logger.info("Message broadcast completed successfully.");
	        }
	    } catch (Exception e) {
	        logger.error("Error broadcasting message for chatId: {}", chatId, e);
	        sendMessage(chatId, "An error occurred while broadcasting the message.");
	    }
	}

	// ✅ Handle /start Command - Initiate Email Verification
	private void handleStartCommand(String chatId, String firstName) {
	    try {
	        logger.info("Start command received from user: {}", firstName);
	        awaitingEmail.put(chatId, true);

	        // ✅ Send welcome message with T&C link
	        sendMessage(chatId,
	            "By using this chatbot service, you hereby agree to accept the *TERMS & CONDITIONS* of Lincpay Telegram and Chatbot services.\n" +
	            "The terms and conditions are available at Lincpay website:\n" +
	            "🔗 https://lincpay.co.in/\n\n" +
	            "Welcome, " + firstName + " *! Please enter your **Email ID** to verify.*"
	        );
	    } catch (Exception e) {
	        logger.error("Error handling start command for chatId: {}", chatId, e);
	        sendMessage(chatId, "An error occurred while processing your request.");
	    }
	}

	// ✅ Handle Login with Email and Passphrase
	private void loginByEmailAndPassPhase(String chatId, String email, String passPhase, String firstName, String lastName) {
	    try {
	        logger.info("Processing login for email: {} in chatId: {}", email, chatId);

	        // ✅ Stop awaiting email after receiving credentials
	        awaitingEmail.put(chatId, false);

	        // ✅ Validate credentials using UserValidateService
	        String token = userValidateService.getUsernameAndPassword(email, passPhase);

	        // ✅ Handle server error
	        if ("500".equals(token)) {
	            sendMessage(chatId, "⚠️ Internal Server Error. Please try again later.");
	            logger.error("Internal server error during login for email: {}", email);
	        }
	        // ✅ Successful login, store user data
	        else if (token != null) {
	            userTokens.put(chatId, token);
	            sendMessage(chatId, "✅ Verification successful! You can now use the bot.");
	            logger.info("User login successful for email: {}", email);
	        }
	        // ✅ Handle invalid credentials
	        else {
	            sendMessage(chatId, "❌ *Invalid Email ID or PassPhrase.* Please try again.");
	            awaitingEmail.put(chatId, true); // Continue awaiting valid input
	            logger.warn("Invalid email or passphrase for email: {}", email);
	        }
	    } catch (Exception e) {
	        logger.error("Error handling email verification for chatId: {}", chatId, e);
	        sendMessage(chatId, "⚠️ An error occurred while verifying your email. Please try again.");
	    }
	}

	// ✅ Send Message to Chat using Telegram API
	public ResponseEntity<ResponseModel> sendMessage(String chatId, String text) {
	    try {
	        SendMessage message = new SendMessage();
	        message.setChatId(chatId);
	        message.setText(text);
	        message.setParseMode("Markdown"); // ✅ Use Markdown for rich text formatting
	        execute(message);
	        logger.info("Message sent successfully to chatId: {}", chatId);
	    } catch (TelegramApiException e) {
	        logger.error("Error sending message to chatId: {}", chatId, e);
	    }
	    return null;
	}
	// ✅ Fetch and Cache Menu Commands from Database
	@Cacheable("menuCommands")
	public List<BotCommand> fetchMenuCommands() {
	   // logger.info("Fetching menu commands from database...");
	    return addMenuServiceImp.findAllMenuAdmin()
	            .stream()
	            .map(menu -> new BotCommand(menu.getMenuName(), menu.getMenuDescription()))
	            .collect(Collectors.toList());
	}

	// ✅ Initialize and Register Bot Commands on Startup
	@PostConstruct
	public void initCommands() {
	    try {
	  //      logger.info("Initializing bot commands...");

	        botCommands.clear();
	        botCommands.add(new BotCommand("start", "Starts the bot"));
	        botCommands.addAll(fetchMenuCommands()); // ✅ Fetch menu commands dynamically
	        botCommands.add(new BotCommand("help", "Explains how to use the bot"));

	        // ✅ Register commands for group and bot
	        registerCommandsForAll();
//	        logger.info("Telegram bot commands initialized successfully.");
	    } catch (Exception e) {
	        logger.error("Error initializing commands", e);
	    }
	}
	// ✅ Register Bot Commands for Private and Group Chats
	public void registerCommandsForAll() {
	    try {

	        // ✅ Register commands for private chat
	        SetMyCommands privateCommands = new SetMyCommands(botCommands, new BotCommandScopeDefault(), null);
	        execute(privateCommands);

	        // ✅ Register commands for group chats
	        SetMyCommands groupCommands = new SetMyCommands(botCommands, new BotCommandScopeAllGroupChats(), null);
	        execute(groupCommands);
	    } catch (TelegramApiException e) {
	        logger.error("Error registering commands:", e);
	    }
	}

	// ✅ Clear Cache and Refresh Commands
	@CacheEvict(value = "menuCommands", allEntries = true)
	public void refreshCommands() {
	    initCommands(); // ✅ Reload commands dynamically
	}

	// ✅ Handle User Menu Selection
	public void handleMenuSelection(String chatId, String receivedText, String token, String firstName, String lastName) {
	    try {
	        logger.info("Handling menu selection: '{}' by user: {}", receivedText, firstName);

	        switch (receivedText.toLowerCase()) {
	            case "/withdraw_request":
	                handleWithdrawRequest(chatId, token);
	                break;
	            case "/payin_limit":
	                fetchMidForButton(chatId, token, "payin_limit");
	                break;
	            case "/payout_balance":
	                fetchMidForButton(chatId, token, "payoutBalance");
	                break;
	            case "/broad_cast":
	                sendMessage(chatId, "Enter Message for Broadcast to All Groups:\n(Type 'exit' to cancel)");
	                awaitingBoardCastMessage.put(chatId, true);
	                break;
	            case "/help":
	                sendHelpMessage(chatId);
	                break;
	            default:
					if(receivedText.startsWith("/"))
					{
						sendMessage(chatId, "Invalid option. Please type '/help' for available commands. - " +
								"UserName: " + firstName + ", chatId: " + chatId);
					}
					logger.info("Ignored unrecognized command: '{}' from user: {}", receivedText, firstName);
	        }
	    } catch (Exception e) {
	        logger.error("Error handling menu selection for chatId: {}", chatId, e);
	        sendMessage(chatId, "An error occurred while processing your request.");
	    }
	}

	// ✅ Fetch MID List and Create Inline Button Options
	public void fetchMidForButton(String chatId, String token, String action) {
	    try {
	        List<FetchMidDataResponse> responses = apiCallerService.fetchMid(token);

	        if (responses.isEmpty()) {
				sendMessage(chatId, "⚠️ Your Token is Expired. Please Enter /start Command.");
				logger.warn("No MID data found for token: {}", responses);
	            return;
	        }

	        // ✅ Prepare message with inline buttons for MID selection
	        SendMessage message = new SendMessage();
	        message.setChatId(chatId);
	        message.setText("🔹 *Select a MID:*");
	        message.enableMarkdown(true);

	        // ✅ Create inline keyboard markup
	        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
	        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

	        for (FetchMidDataResponse response : responses) {
	            InlineKeyboardButton button = new InlineKeyboardButton();
	            button.setText(response.getBusinessName() + " (" + response.getMid() + ")");
	            button.setCallbackData(action + response.getMid());
	            midBusinessNameMap.put(response.getMid(), response.getBusinessName());

	            List<InlineKeyboardButton> row = new ArrayList<>();
	            row.add(button);
	            keyboard.add(row);
	        }

	        inlineKeyboardMarkup.setKeyboard(keyboard);
	        message.setReplyMarkup(inlineKeyboardMarkup);

	        execute(message);
	        logger.info("MID selection buttons sent successfully.");
	    } catch (TelegramApiException e) {
	        logger.error("Error sending MID selection buttons to chatId: {}", chatId, e);
	    }
	}

	// ✅ Handle Withdrawal Request and Display Data
	private void handleWithdrawRequest(String chatId, String token) {
	    try {
	        logger.info("Processing withdrawal request for chatId: {}", chatId);
	        Object responseData = apiCallerService.callApi(token);

			logger.info("Processing withdrawal request for chatId: {}", responseData);
	        if (responseData.toString().equals("500")) {
	            sendMessage(chatId, "⚠️ Your Token is Expired. Please Enter /start Command.");
	            logger.warn("Token expired for chatId: {}", chatId);
	        } else {
	            String response = responseData.toString();
	            int chunkSize = 4000;

	            for (int i = 0; i < response.length(); i += chunkSize) {
	                sendMessage(chatId, response.substring(i, Math.min(response.length(), i + chunkSize)));
	                Thread.sleep(500);
	            }
	            logger.info("Withdrawal data sent successfully.");
	        }
	    } catch (Exception e) {
	        logger.error("Error processing withdrawal request for chatId: {}", chatId, e);
	        sendMessage(chatId, "An error occurred while processing your withdrawal request.");
	    }
	}

	// ✅ Send Help Message with Available Commands
	private void sendHelpMessage(String chatId) {
	    sendMessage(chatId, "Available commands:\n" +
	            " * /start * - Start for verifying yourself\n" +
	            " * /withdraw_request * - Withdraw request\n" +
	            " * /payin_limit * - Check payin limit\n" +
	            " * /payout_balance * - Check payout balance\n" +
	            " * /help * - Help menu");
	    logger.info("Help message sent to chatId: {}", chatId);
	}

//	// ✅ Send Withdrawal Message with Approve/Reject Buttons
//	public void sendWithdrawalMessageWithButton(String chatId, WithdrawalRequestReceive withdrawalRequest) {
//	    try {
//	        logger.info("Sending withdrawal message with buttons for withdrawalId: {}", withdrawalRequest.getWithdrawalId());
//
//	        // ✅ Create inline keyboard markup
//	        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
//	        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
//
//	        String uniqueApproveCallback = "approve_" + withdrawalRequest.getWithdrawalId();
//	        String uniqueRejectCallback = "reject_" + withdrawalRequest.getWithdrawalId();
//
//	        InlineKeyboardButton approveButton = new InlineKeyboardButton();
//	        approveButton.setText("✅ Approve");
//	        approveButton.setCallbackData(uniqueApproveCallback);
//
//	        InlineKeyboardButton rejectButton = new InlineKeyboardButton();
//	        rejectButton.setText("❌ Reject");
//	        rejectButton.setCallbackData(uniqueRejectCallback);
//
//	        buttons.add(Arrays.asList(approveButton, rejectButton));
//	        markup.setKeyboard(buttons);
//
//	        // ✅ Create and send message with buttons
//	        SendMessage message = new SendMessage();
//	        message.setChatId(chatId);
//	        message.setText(withdrawalRequest.toString());
//	        message.setParseMode("Markdown");
//	        message.setReplyMarkup(markup);
//
//	        Message sentMessage = execute(message);
//	        withdrawalMessageIds.put(chatId + "_" + withdrawalRequest.getWithdrawalId(), sentMessage.getMessageId());
//	        logger.info("Withdrawal message sent with buttons for withdrawalId: {}", withdrawalRequest.getWithdrawalId());
//	    } catch (TelegramApiException e) {
//	        logger.error("Error sending withdrawal message with buttons", e);
//	    }
//	}

	// ✅ Delete Message from Chat
	private void deleteMessage(String chatId, Integer messageId) {
	    if (messageId == null) {
	        logger.warn("Message ID is null. Cannot delete the message.");
	        return;
	    }

	    try {
	        DeleteMessage deleteMessage = new DeleteMessage();
	        deleteMessage.setChatId(chatId);
	        deleteMessage.setMessageId(messageId);
	        execute(deleteMessage);
	        logger.info("Message deleted successfully from chat: {}", chatId);
	    } catch (Exception e) {
	        logger.error("Error deleting message in chat: {} - {}", chatId, e.getMessage());
	    }
	}

	// ✅ Mark Withdrawal Request as Processed (Approved/Rejected)
	private void markAsProcessed(String chatId, String withdrawalId, boolean isApproved, String userName) {
	    String messageKey = chatId + "_" + withdrawalId;
	    Integer withdrawalMessageId = withdrawalMessageIds.get(messageKey);

	    if (withdrawalMessageId != null) {
	        logger.info("Marking withdrawalId: {} as processed.", withdrawalId);

	        // ✅ Update message with approval/rejection status
	        EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
	        editMarkup.setChatId(chatId);
	        editMarkup.setMessageId(withdrawalMessageId);

	        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
	        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

	        // ✅ Create "Marked as Approved/Rejected" button
	        InlineKeyboardButton doneButton = new InlineKeyboardButton();
	        if (isApproved) {
	            doneButton.setText("✅ Approved by @" + userName);
	        } else {
	            doneButton.setText("❌ Rejected by @" + userName);
	        }
	        doneButton.setCallbackData("done_" + withdrawalId);

	        keyboard.add(Collections.singletonList(doneButton));
	        markup.setKeyboard(keyboard);
	        editMarkup.setReplyMarkup(markup);

	        try {
	            execute(editMarkup);
	            withdrawalMessageIds.remove(messageKey);
	            logger.info("Withdrawal request updated successfully with status for withdrawalId: {}", withdrawalId);
	        } catch (TelegramApiException e) {
	            logger.error("Failed to update withdrawal message for chatId: {}", chatId, e);
	        }
	    } else {
	        sendMessage(chatId, "⚠️ Error: Cannot find the original withdrawal request.");
	        logger.warn("Original withdrawal request not found for withdrawalId: {}", withdrawalId);
	    }
	}

	// ✅ Get User Token by ChatId
	public String getUserToken(String chatId) {
	    return userTokens.get(chatId);
	}

	// ✅ Check if User is Group Admin
	private boolean isGroupAdmin(String chatId, Long userId) {
	    try {
	        ChatMember chatMember = execute(new GetChatMember(chatId, userId));
	        boolean isAdmin = chatMember.getStatus().equals("administrator") || chatMember.getStatus().equals("creator");
	        logger.info("Checked admin status for userId: {} in chatId: {} - isAdmin: {}", userId, chatId, isAdmin);
	        return isAdmin;
	    } catch (TelegramApiException e) {
	        logger.error("Error checking admin status for userId: {} in chatId: {}", userId, chatId, e);
	        return false;
	    }
	}


	public void sendPhoto(String chatId, MultipartFile file, String caption) {
		try {
			String url = "https://api.telegram.org/bot" + botToken + "/sendPhoto";
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("chat_id", chatId);
			body.add("photo", new MultipartFileResource(file));
			body.add("caption", caption); // Add caption here

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
			new RestTemplate().postForEntity(url, request, String.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to send photo to Telegram", e);
		}
	}

	public void sendDocument(String chatId, MultipartFile file, String caption) {
		try {
			String url = "https://api.telegram.org/bot" + botToken + "/sendDocument";
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("chat_id", chatId);
			body.add("document", new MultipartFileResource(file));
			body.add("caption", caption); // Add caption here

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
			new RestTemplate().postForEntity(url, request, String.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to send document to Telegram", e);
		}
	}

	public void sendReplyMessage(String chatId, String text, Integer replyToMessageId) {
		String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

		Map<String, Object> body = new HashMap<>();
		body.put("chat_id", chatId);
		body.put("text", text);
		body.put("reply_to_message_id", replyToMessageId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

		new RestTemplate().postForEntity(url, request, String.class);
	}

}