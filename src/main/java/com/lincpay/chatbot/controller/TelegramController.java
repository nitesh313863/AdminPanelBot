//package com.lincpay.chatbot.controller;
//
//import com.lincpay.chatbot.dto.Request.GetPayinSidLimit;
//import com.lincpay.chatbot.dto.Request.PrivilegeRequestDTO;
//import com.lincpay.chatbot.dto.Request.SendAlertMeassegeInMidGroup;
//import com.lincpay.chatbot.dto.Request.SendBroadCastGroupRequest;
//import com.lincpay.chatbot.dto.Request.WithdrawalRequestReceive;
//import com.lincpay.chatbot.dto.response.FetchResponseAdminGroupMember;
//import com.lincpay.chatbot.dto.response.GroupMemberResponseDTO;
//import com.lincpay.chatbot.dto.response.GroupResponseDTO;
//import com.lincpay.chatbot.dto.response.ResponseModel;
//import com.lincpay.chatbot.entities.AddMenuTelegramAdminPanel;
//import com.lincpay.chatbot.entities.Group;
//import com.lincpay.chatbot.entities.TelegramAdminGroup;
//import com.lincpay.chatbot.entities.TelegramAdminUser;
//import com.lincpay.chatbot.entities.TelegramMerchantGroup;
//import com.lincpay.chatbot.serivce.AddMenuService;
//import com.lincpay.chatbot.serivce.GroupMemberService;
//import com.lincpay.chatbot.serivce.TelegramGroupService;
//import com.lincpay.chatbot.serivce.TelegramAdminService;
//import com.lincpay.chatbot.serviceimp.TelegramBotService;
//import com.lincpay.chatbot.serviceimp.TelegramGroupServiceImp;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//
//import java.util.List;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
//
//@RestController
//@CrossOrigin("*")
//@RequestMapping("/telegram")
//public class TelegramController {
//	
//    // ✅ Logger for tracking actions and errors
//    private static final Logger logger = LoggerFactory.getLogger(TelegramController.class);
//    @Autowired
//    TelegramBotService telegramBotService;
//
//    @Autowired
//    AddMenuService addMenuService;
//    
//    @Autowired
//    GroupMemberService groupMemberService;
//
//    @Autowired
//    TelegramGroupService telegramGroupService;
//    
//    @Autowired
//    TelegramAdminService telegramAdminService;
//    
//    @PostMapping("/addMerchantGroup")
//    public ResponseEntity<ResponseModel> addMerchantGroup(@RequestBody TelegramMerchantGroup telegramMerchantGroup)
//    {
//    	return telegramGroupService.addMerchantGroup(telegramMerchantGroup);
//    }
//    @GetMapping("/getAllMerchantGroup")
//    public ResponseEntity<ResponseModel<List<TelegramMerchantGroup>>> getAllMerchantGroups()
//    {
//    	return telegramGroupService.getAllMerchantGroup();
//    }
//    @PostMapping("/addAdminGroup")
//    public ResponseEntity<ResponseModel> addAdminGroup(@RequestBody TelegramAdminGroup telegramAdminGroup)
//    {
//    	return telegramGroupService.addAdminGroupData(telegramAdminGroup);
//    }
//    
//    @PostMapping("/sendBroadcast")
//    public ResponseEntity<String> sendBroadCastMessage(@RequestBody SendBroadCastGroupRequest request )
//    {
//
//        if (request.getGroupChatIds() == null || request.getGroupChatIds().isEmpty()) {
//            return ResponseEntity.badRequest().body("No groups selected.");
//        }
//
//        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
//            return ResponseEntity.badRequest().body("Message cannot be empty.");
//        }
//
//        // Process the request - send the broadcast message to selected groups
//        for (String groupChatId : request.getGroupChatIds()) {
//        	telegramBotService.sendMessage(groupChatId, request.getMessage());
//            // Here, integrate your service to send messages via Telegram API
//        }
//        return ResponseEntity.ok("Broadcast sent successfully!");
//    }
//    @PostMapping("/sendWithdrawalRequestMessage")
//    public ResponseEntity<String> sendWithdrawalRequestMessage(@RequestBody WithdrawalRequestReceive withdrawalRequest) {
//        try {
//            // ✅ Fetch all admin groups to send the withdrawal request
//            List<TelegramAdminGroup> telegramAdminGroups = telegramGroupService.getAllAdminGroup();
//            
//            // 🔄 Loop through all admin groups and send the message
//            for (TelegramAdminGroup adminGroup : telegramAdminGroups) {
//                String chatIdAdmin = adminGroup.getGroupChatId(); // Get group chat ID dynamically
//                
//                // ✅ Send withdrawal request with approve/reject buttons
//                telegramBotService.sendWithdrawalMessageWithButton(chatIdAdmin, withdrawalRequest);
//
//                // 🔍 Check if the admin is authenticated (token exists for this chatId)
//                String token = telegramBotService.getUserToken(chatIdAdmin);
//                if (token == null) {
//                    // ⚠️ If not logged in, prompt login after message is sent
//                    telegramBotService.sendMessage(chatIdAdmin, "⚠️ You are not logged in! Please use /start to login.");
//                }
//            }
//            
//            // 🎉 Success message if everything went well
//            return ResponseEntity.ok("✅ Withdrawal request sent successfully to all admin groups.");
//            
//        } catch (Exception e) {
//            // ⚠️ Log error and return failure message
//            logger.error("Error occurred while sending withdrawal request: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("❌ Failed to send withdrawal request. Please try again later.");
//        }
//    }
//
//
//    @PostMapping("/addMenu")
//    public ResponseEntity<ResponseModel> addMenus(@RequestBody AddMenuTelegramAdminPanel addMenuTelegram)
//    {
//        ResponseEntity<ResponseModel> response=addMenuService.addMenue(addMenuTelegram);
//        return response;
//    }
//    
//    @PostMapping("/showPayinSidLimitTelegram")
//    public ResponseEntity<String> showPayinSidLimitTelegramBot(@RequestBody List<GetPayinSidLimit> getPayinSidLimitList) {
//        try {
//            // ✅ Fetch all admin groups to send the Payin SID limit updates
//            List<TelegramAdminGroup> telegramAdminGroups = telegramGroupService.getAllAdminGroup();
//
//            // ✅ Check if admin groups are found
//            if (telegramAdminGroups.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body("No admin groups found. Cannot send Payin SID limit updates.");
//            }
//
//            // 🔄 Loop through all admin groups and send the Payin SID limit to each
//            for (TelegramAdminGroup adminGroup : telegramAdminGroups) {
//                String chatIdAdmin = adminGroup.getGroupChatId(); // Get group chat ID dynamically
//
//                // 🔄 Loop through each Payin SID limit and send the message
//                for (GetPayinSidLimit getPayinSidLimit : getPayinSidLimitList) {
//                    telegramBotService.sendMessage(chatIdAdmin, getPayinSidLimit.toString());
//                }
//            }
//
//            // 🎉 Return success response
//            return ResponseEntity.ok("Payin SID limit updates sent successfully to all admin groups.");
//
//        } catch (Exception e) {
//            // ❌ Handle any exceptions that occur
//            logger.error("Error occurred while sending Payin SID limit updates: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(" Error occurred while sending Payin SID limit updates. Please try again.");
//        }
//    }
//    @PostMapping("sendAlertMessageInMerchantGroup")
//    public ResponseEntity<String> sendAlertMessageInMerchantGroup(@RequestBody SendAlertMeassegeInMidGroup sendAlertMeassegeInMidGroup) {
//        try {
//            // ✅ Fetch merchant group by MID
//            TelegramMerchantGroup telegramMerchantGroup = telegramGroupService.getMerchantGroupByMid(sendAlertMeassegeInMidGroup);
//
//            // ✅ Check if group is found
//            if (telegramMerchantGroup != null && telegramMerchantGroup.getGroupChatId() != null) {
//                // ✅ Send message to the correct group
//                telegramBotService.sendMessage(telegramMerchantGroup.getGroupChatId(), sendAlertMeassegeInMidGroup.toString());
//                return ResponseEntity.ok(" Alert message sent successfully to group: " + telegramMerchantGroup.getGroupName());
//            } else {
//                // ❌ Group not found
//                return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(" No group found for MID: " + sendAlertMeassegeInMidGroup.getMid());
//               
//            }
//        } catch (Exception e) {
//            // ❌ Error handling
//           
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(" Error while sending the alert message. Please try again.");
//        }
//    }
//    
//    @PostMapping("/addGroupMemberByGroupIdAndMid")
//    public ResponseEntity<ResponseModel<List<GroupMemberResponseDTO>>> addGroupMemberByGroupIdAndMid(@RequestParam("groupId") Long groupId, 
//    	    @RequestParam("mid") String mid) {      
//        return groupMemberService.getGroupMemberByGroupId(groupId,mid);
//    }
//    
//    @GetMapping("/getALLGroupName")
//    public ResponseEntity<ResponseModel<List<GroupResponseDTO>>> getAllGroupName() {
//        return groupMemberService.fetchAllGroupName();
//    }
//    @GetMapping("/getALLGroupMemberByGroupId")
//    public ResponseEntity<ResponseModel<List<GroupMemberResponseDTO>>> getALLGroupMemberByGroupId(@RequestParam Long groupid) { 
//        return groupMemberService.fetchAllGroupMember(groupid); 
//    }
//    
//    @PostMapping("/updateGroupMemberPrivileges")
//    public ResponseEntity<ResponseModel> updateGroupMemberPrivileges(
//            @RequestParam Long groupId,
//            @RequestBody List<PrivilegeRequestDTO> privileges) {
//        return groupMemberService.updateGroupMemberPrivileges(groupId, privileges);
//    }
//    
//    @GetMapping("/getALLGroupAdminMemberByGroupId")
//    public ResponseEntity<ResponseModel<List<FetchResponseAdminGroupMember>>> getGroupAdmins(@RequestParam Long groupId) {
//    	System.out.println(groupId);
//        return groupMemberService.getGroupAdmins(groupId);
//    }
//    
//    @PostMapping("/addTelegramAdminUser")
//    public ResponseEntity<ResponseModel> addTelegramAdmin(@RequestBody TelegramAdminUser telegramAdminUser ) {   
//        return telegramAdminService.addAdminUserData(telegramAdminUser);
//    }
//}
package com.lincpay.chatbot.controller;


