package com.lincpay.chatbot.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.lincpay.chatbot.dto.Request.MerchantGroupEditRequestDto;
import com.lincpay.chatbot.dto.Request.MerchantMessageRequestDto;
import com.lincpay.chatbot.dto.Request.ReplyMessageDto;
import com.lincpay.chatbot.entities.MerchantChat;
import com.lincpay.chatbot.repository.AdminReplyChatRepo;
import com.lincpay.chatbot.repository.MerchantChatRepo;
import com.lincpay.chatbot.serivce.MerchantGroupChatAdminPanelService;
import com.lincpay.chatbot.serviceimp.TelegramBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.TelegramMerchantGroup;
import com.lincpay.chatbot.serivce.TelegramGroupService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@CrossOrigin("*")
@RequestMapping("/merchant-group")
public class MerchantGroupController {
    @Autowired
    TelegramBotService telegramBotService;
    @Autowired
    AdminReplyChatRepo adminReplyChatRepo;

    private static final Logger logger = LoggerFactory.getLogger(MerchantGroupController.class);

    @Autowired
    MerchantGroupChatAdminPanelService merchantGroupChatAdminPanelService;

    @Autowired
    MerchantChatRepo merchantChatRepo;

    private final TelegramGroupService telegramGroupService;

    public MerchantGroupController(TelegramGroupService telegramGroupService) {
        this.telegramGroupService = telegramGroupService;
    }

    @PostMapping("/addMerchantGroup")
    public ResponseEntity<ResponseModel> addMerchantGroup(@RequestBody TelegramMerchantGroup group) {
        return telegramGroupService.addMerchantGroup(group);
    }

    @GetMapping("/getAllMerchantGroup")
    public ResponseEntity<ResponseModel<List<TelegramMerchantGroup>>> getAllMerchantGroups() {
        return telegramGroupService.getAllMerchantGroup();
    }

    @GetMapping("/getMerchantGroupByMid/{mid}")
    public ResponseEntity<ResponseModel<TelegramMerchantGroup>> getMerchantGroupByMid(@PathVariable("mid") String mid) {
        return telegramGroupService.fetchMerchantGroupByMid(mid);
    }

    @PutMapping("editMerchantGroupByMid/{mid}")
    public ResponseEntity<ResponseModel> editMerchantGroupByMid(@PathVariable("mid") String mid,
                                                                @Valid @RequestBody MerchantGroupEditRequestDto group) {
        return telegramGroupService.editmerchantGroup(mid, group);
    }

    @GetMapping("/getMerchantChat")
    public ResponseEntity<ResponseModel> getMerchantChat(@RequestParam String chatId) {
        return merchantGroupChatAdminPanelService.getMerchantChatsByChatId(chatId);
    }

    @PostMapping("/sendMerchantMessage")
    public ResponseEntity<ResponseModel> sendMerchantMessage(@RequestBody MerchantMessageRequestDto dto) {
        try {

            String finalMessage = "\uD83D\uDC64 " + dto.getUsername() + ":\n\n" + dto.getMessage();

            telegramBotService.sendMessage(dto.getChatId(), finalMessage);

            merchantGroupChatAdminPanelService.adminReplyStoreDb(dto.getChatId(), finalMessage);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseModel("Message sent successfully", "success", HttpStatus.OK.value()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel("Error sending message", "internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }


    @GetMapping("/getAdminChatReply")
    public ResponseEntity<ResponseModel> getAdminChatReply(@RequestParam String chatId) {
        return merchantGroupChatAdminPanelService.getAdminReplyChat(chatId);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadPdf(@PathVariable Long id) throws IOException {
        Optional<MerchantChat> chat = merchantChatRepo.findById(id);
        if (!chat.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        MerchantChat merchantChat = chat.get();
        String filePathFromDb = merchantChat.getDocFilePath();  // e.g., "D:/testing/file_16.pdf"
        // Normalize the path to ensure it's valid and safe
        Path path = Paths.get(filePathFromDb).normalize();

        // Check if the file exists
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }
        // Read the file into a ByteArrayResource
        byte[] fileBytes = Files.readAllBytes(path);
        ByteArrayResource resource = new ByteArrayResource(fileBytes);
        // Return the file in the HTTP response
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName() + "\"")
                .contentLength(fileBytes.length)
                .body(resource);
    }

    @PostMapping(value = {"/merchantChatSendPhoto", "/merchantChatSendDocument"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseModel> uploadFileToTelegramGroup(
            @RequestParam("file") MultipartFile file,
            @RequestParam("chatId") String chatId,
            @RequestParam("username") String senderName,
            @RequestParam(value = "caption", required = false) String caption,
            HttpServletRequest request) {

        String originalFileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        String requestURI = request.getRequestURI();
        String fileExtension = originalFileName != null ?
                originalFileName.substring(originalFileName.lastIndexOf(".") + 1) : "";

        try {
            String fullCaption = "👤 " + senderName + "\n-------------------\n" + (caption != null ? caption : "");
            byte[] fileBytes = file.getBytes();

            if (requestURI.endsWith("merchantChatSendPhoto")) {
                logger.info("Sending image to Telegram: {}", originalFileName);

                // Send to Telegram
                telegramBotService.sendPhoto(chatId, file, fullCaption);

                // Store in database
                merchantGroupChatAdminPanelService.adminReplyStoreDocumentDb(
                        chatId, file, "photo", contentType, originalFileName,caption);

                // Prepare response
                String mimeType = contentType != null ? contentType : "image/jpeg";
                String base64Data = "data:" + mimeType + ";base64," +
                        Base64.getEncoder().encodeToString(fileBytes);

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("status", "success");
                responseData.put("chatId", chatId);
                responseData.put("fileName", originalFileName);
                responseData.put("fileType", "image");
                responseData.put("fileData", base64Data);
                responseData.put("fileSize", file.getSize());
                responseData.put("caption", fullCaption);
                responseData.put("timestamp", new Date().getTime());
                responseData.put("fileExtension", fileExtension);
                responseData.put("msgType","photo");
                responseData.put("senderType","admin");

                return ResponseEntity.ok(new ResponseModel<>(
                        "Photo sent successfully",
                        "success",
                        HttpStatus.OK.value(),
                        responseData
                ));

            } else if (requestURI.endsWith("merchantChatSendDocument")) {
                logger.info("Sending document to Telegram: {}", originalFileName);

                // Send to Telegram
                telegramBotService.sendDocument(chatId, file, fullCaption);

                // Store in database
                merchantGroupChatAdminPanelService.adminReplyStoreDocumentDb(
                        chatId, file, "document", contentType, originalFileName,caption);

                // Prepare response
                String mimeType = contentType != null ? contentType : "application/octet-stream";
                String base64Data = "data:" + mimeType + ";base64," +
                        Base64.getEncoder().encodeToString(fileBytes);

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("status", "success");
                responseData.put("chatId", chatId);
                responseData.put("fileName", originalFileName);
                responseData.put("fileType", "document");
                responseData.put("fileData", base64Data);
                responseData.put("fileSize", file.getSize());
                responseData.put("caption", fullCaption);
                responseData.put("timestamp", new Date().getTime());
                responseData.put("fileExtension", fileExtension);
                responseData.put("mimeType", mimeType);
                responseData.put("msgType","document");
                responseData.put("senderType","admin");

                return ResponseEntity.ok(new ResponseModel<>(
                        "Document sent successfully",
                        "success",
                        HttpStatus.OK.value(),
                        responseData
                ));
            }

        } catch (Exception e) {
            logger.error("Exception occurred while processing file upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(
                            "Error occurred while processing file",
                            "internal_server_error",
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    ));
        }

        return ResponseEntity.badRequest().body(new ResponseModel<>(
                "Invalid endpoint",
                "bad_request",
                HttpStatus.BAD_REQUEST.value()
        ));
    }
    @PostMapping("/sendMessageWithReply")
    public ResponseEntity<ResponseModel> sendMessageWithReply(@RequestBody ReplyMessageDto dto) {
        try {
            System.out.println(dto);
            telegramBotService.sendReplyMessage(dto.getChatId(), dto.getText(), dto.getReplyToMessageId());
            return ResponseEntity.ok(new ResponseModel("Reply sent", "success", 200));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseModel("Failed to send reply", "error", 500));
        }
    }
}
