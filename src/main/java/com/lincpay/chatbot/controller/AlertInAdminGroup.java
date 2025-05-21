package com.lincpay.chatbot.controller;

import com.lincpay.chatbot.entities.TelegramAdminGroup;
import com.lincpay.chatbot.serivce.TelegramGroupService;
import com.lincpay.chatbot.serviceimp.TelegramBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class AlertInAdminGroup {
    @Autowired
    TelegramGroupService telegramGroupService;
    @Autowired
    TelegramBotService telegramBotService;

    @PostMapping("/get-ThreeHr-Success-txn")
    public ResponseEntity<String> receiveSummary(@RequestBody Map<String, Object> payload) {
        try {
            List<TelegramAdminGroup> telegramAdminGroups = telegramGroupService.getAllAdminGroupId();

            if (telegramAdminGroups.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No admin groups configured.");
            }

            int midTotalCount = ((Number) payload.get("midTotalCount")).intValue();
            int totalTxnCount = ((Number) payload.get("totalTxnCount")).intValue();
            double totalAmount = Double.parseDouble(payload.get("totalAmount").toString());

            StringBuilder message = new StringBuilder();
            message.append("💸 *Payout Summary (3 Hours)* 💸\n\n")
                    .append("🏢 *Total MIDs:* ").append(midTotalCount).append("\n")
                    .append("🔁 *Total Txns:* ").append(totalTxnCount).append("\n")
                    .append("💰 *Total Amount:* ₹").append(String.format("%,.2f", totalAmount)).append("\n\n")
                    .append("📋 *Breakdown by MID:*\n");

            List<Map<String, Object>> midSummaries = (List<Map<String, Object>>) payload.get("midSummaries");
            for (Map<String, Object> midData : midSummaries) {
                String mid = midData.get("mid").toString();
                int txnCount = ((Number) midData.get("txnCount")).intValue();
                double amount = Double.parseDouble(midData.get("totalAmount").toString());

                message.append("➡️ MID: `").append(mid)
                        .append("` | Txns: ").append(txnCount)
                        .append(" | ₹").append(String.format("%,.2f", amount)).append("\n");
            }

            for (TelegramAdminGroup adminGroup : telegramAdminGroups) {
                telegramBotService.sendMessage(adminGroup.getGroupChatId(), message.toString());
            }

            return ResponseEntity.ok("Alert sent successfully.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/payin-txn-summary")
    public ResponseEntity<String> payinTxnSummary(@RequestBody Map<String, Object> payload) {
        try {
            List<TelegramAdminGroup> telegramAdminGroups = telegramGroupService.getAllAdminGroupId();

            if (telegramAdminGroups.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No admin groups configured.");
            }

            int totalTxnCount = ((Number) payload.get("totalTxn")).intValue();
            int midTotalCount = ((Number) payload.get("totalMid")).intValue();
            double totalAmount = Double.parseDouble(payload.get("totalAmount").toString());

            StringBuilder message = new StringBuilder();
            message.append("💸 *Pay-In Summary (3 Hours)* 💸\n\n")
                    .append("🏢 *Total MIDs:* ").append(midTotalCount).append("\n")
                    .append("🔁 *Total Txns:* ").append(totalTxnCount).append("\n")
                    .append("💰 *Total Amount:* ₹").append(String.format("%,.2f", totalAmount)).append("\n\n")
                    .append("📋 *Breakdown by MID/SID:*\n");

            List<Map<String, Object>> details = (List<Map<String, Object>>) payload.get("details");

            for (Map<String, Object> midEntry : details) {
                String mid = midEntry.get("mid").toString();
                message.append("➡️ *MID:* `").append(mid).append("`\n");

                List<Map<String, Object>> sids = (List<Map<String, Object>>) midEntry.get("sids");
                for (Map<String, Object> sidEntry : sids) {
                    String sid = sidEntry.get("sid").toString();
                    int sidTxnCount = ((Number) sidEntry.get("totalTxn")).intValue();
                    double sidAmount = Double.parseDouble(sidEntry.get("totalCount").toString());

                    message.append("   ┗ 📌 *SID:* `").append(sid)
                            .append("` | Txns: ").append(sidTxnCount)
                            .append(" | ₹").append(String.format("%,.2f", sidAmount)).append("\n");
                }

                message.append("\n"); // Add spacing between MIDs
            }

            for (TelegramAdminGroup adminGroup : telegramAdminGroups) {
                telegramBotService.sendMessage(adminGroup.getGroupChatId(), message.toString());
            }

            return ResponseEntity.ok("Pay-in summary sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/AlertCode422Inter")
    public ResponseEntity<String> AlertSummary() {
//       logger.info("Entered into API");
        System.out.println("bank side issue");
//       return "null";
        try {
            // Fetch all admin groups
            List<TelegramAdminGroup> telegramAdminGroups = telegramGroupService.getAllAdminGroupId();
//                      If no admin groups are found
      if (telegramAdminGroups.isEmpty()) {
//         logger.warn("No admin groups found to send the alert for MID: {}", notification10FailedTxnRequestDto.getMid());
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No admin groups found.");
      }

            // Send messages to all admin groups
            for (TelegramAdminGroup adminGroup : telegramAdminGroups) {
                String chatIdAdmin = adminGroup.getGroupChatId();
                telegramBotService.sendMessage(chatIdAdmin, "⚠ ALERT: Bank End Issue Detected!");
            }

            // Log success and return response

            return ResponseEntity.ok("Alert sent successfully.");

        } catch (Exception e) {
            // Log error and return error response
     //       logger.error("Something went wrong",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error occurred.");
        }
    }
}


