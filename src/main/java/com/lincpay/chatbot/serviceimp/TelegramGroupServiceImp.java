package com.lincpay.chatbot.serviceimp;

import java.util.List;

import com.lincpay.chatbot.dto.Request.MerchantGroupEditRequestDto;
import com.lincpay.chatbot.entities.TelegramAdminGroup;
import com.lincpay.chatbot.entities.TelegramGroupMessage;
import com.lincpay.chatbot.repository.TelegramAdminGroupRepo;
import com.lincpay.chatbot.repository.TelegramGroupMessageRepo;
import com.lincpay.chatbot.repository.TelegramMerchantGroupRepo;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.lincpay.chatbot.constant.ApplicationConstant;
import com.lincpay.chatbot.dto.Request.SendAlertMeassegeInMidGroup;
import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.TelegramMerchantGroup;
import com.lincpay.chatbot.serivce.TelegramGroupService;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service
public class TelegramGroupServiceImp implements TelegramGroupService {

    // ✅ Logger for tracking actions and errors
    private static final Logger logger = LoggerFactory.getLogger(TelegramGroupServiceImp.class);

    @Autowired
    TelegramMerchantGroupRepo telegramGroupRepo;

    @Autowired
    TelegramGroupMessageRepo telegramGroupMessageRepo;
    
    @Autowired
    TelegramAdminGroupRepo telegramAdminGroupRepo;

    @Override
    public ResponseEntity<ResponseModel> addMerchantGroup(TelegramMerchantGroup telegramMerchantGroup) {
        try {

            // ✅ Validate group name and chat ID
            if (telegramMerchantGroup.getMid()!=null&&!telegramMerchantGroup.getGroupName().equals("") && !telegramMerchantGroup.getGroupChatId().equals("")
                    && telegramMerchantGroup.getGroupChatId().startsWith("-")) {              
                // ✅ Save group to database
                telegramGroupRepo.save(telegramMerchantGroup);
                logger.info("Merchant Group added successfully");

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseModel("Merchant Group Added", "success", HttpStatus.CREATED.value()));
            } else {
                logger.warn("Invalid input - Group name or Chat ID is missing or invalid.");
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(new ResponseModel("Input Invalid", "Not Accept", HttpStatus.NOT_ACCEPTABLE.value()));
            }
        } catch (Exception e) {
            logger.error("Error occurred while adding Telegram group: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel("Error", "Exception Occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * ✅ Get all Telegram groups from the database.
     *
     * @return ResponseEntity with ResponseModel containing list of TelegramMerchantGroup.
     */
    public ResponseEntity<ResponseModel<List<TelegramMerchantGroup>>> getAllMerchantGroup() {
        try {
            List<TelegramMerchantGroup> telegramGroups = telegramGroupRepo.findAll();

            if (telegramGroups != null && !telegramGroups.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseModel<List<TelegramMerchantGroup>>("Data Fetch", "success",
                                HttpStatus.OK.value(), telegramGroups));
            } else {
                logger.warn("No Telegram groups found in the database.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel<List<TelegramMerchantGroup>>("Data Not Found", "Not Found",
                                HttpStatus.NOT_FOUND.value(), null));
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching Telegram groups: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>("Error", "Exception Occurred", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            null));
        }
    }

    /**
     * ✅ Get all Telegram groups for broadcast purposes.
     *
     * @return List of TelegramMerchantGroup for broadcast.
     */
    @Override
    public List<TelegramMerchantGroup> getAllGroupForBarodCast() {
        try {
            logger.info("Fetching all Telegram groups for broadcast...");
            List<TelegramMerchantGroup> telegramGroups = telegramGroupRepo.findAll();

            if (telegramGroups.isEmpty()) {
                logger.warn("No groups found for broadcast.");
            } else {
                logger.info("groups found for broadcast.");
            }

            return telegramGroups;
        } catch (Exception e) {
            logger.error("Error occurred while fetching groups for broadcast: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * ✅ Store messages received from group chats in the database.
     *
     * @param chatId       Group chat ID.
     * @param receivedText Message received.
     * @param firstName    First name of the sender.
     * @param lastName     Last name of the sender.
     */
    @Override
    public void messageStoreInDb(String chatId, String receivedText, String firstName, String lastName) {
        try {
            // ✅ Fetch group details by chat ID
            TelegramMerchantGroup telegramGroup = telegramGroupRepo.findByGroupChatId(chatId);

            if (telegramGroup != null) {
                // ✅ Save message to database
                TelegramGroupMessage telegramGroupMessage = new TelegramGroupMessage();
                telegramGroupMessage.setGroupName(telegramGroup.getGroupName());
                telegramGroupMessage.setUserName(firstName);
                telegramGroupMessage.setUserLastName(lastName);
                telegramGroupMessage.setMessage(receivedText);
                telegramGroupMessageRepo.save(telegramGroupMessage);

            } else {
                logger.warn("Group not found for chatId: {}", chatId);
            }
        } catch (Exception e) {
            logger.error("Error occurred while storing message: {}", e.getMessage(), e);
        }
    }

    /**
     * ✅ Get Telegram group by MID for sending alert messages.
     *
     * @param sendAlertMeassegeInMidGroup SendAlertMeassegeInMidGroup object with MID.
     * @return TelegramMerchantGroup corresponding to the provided MID.
     */
    public TelegramMerchantGroup getMerchantGroupByMid(SendAlertMeassegeInMidGroup sendAlertMeassegeInMidGroup) {
        try {
            TelegramMerchantGroup group = telegramGroupRepo.findByMid(sendAlertMeassegeInMidGroup.getMid());
            if (group != null) {
                logger.info("Group '{}' found for MID: {}", group.getGroupName(), sendAlertMeassegeInMidGroup.getMid());
            } else {
                logger.warn("No group found for MID: {}", sendAlertMeassegeInMidGroup.getMid());
            }
            return group;
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error("Internal Server Error",e);
            return null;
        }
    }

	public ResponseEntity<ResponseModel> addAdminGroupData(TelegramAdminGroup telegramAdminGroup) {
		try {
//            logger.info("Received request to add Telegram Admin Group: {}");

            // ✅ Validate group name and chat ID
            if (telegramAdminGroup.getGroupName()!=null && !telegramAdminGroup.getGroupChatId().equals("")
                    && telegramAdminGroup.getGroupChatId().startsWith("-")) {              
                // ✅ Save group to database
            	telegramAdminGroupRepo.save(telegramAdminGroup);
                logger.info("Admin Group added successfully");
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseModel("Admin group Added", "success",HttpStatus.CREATED.value()));
            } else {
                logger.warn("Invalid input - Group name or Chat ID is missing or invalid.");
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(new ResponseModel("Input Invalid",ApplicationConstant.BAD_REQUEST,ApplicationConstant.BADREQUEST));
            }
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error("Internal Server Error",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel("Error",ApplicationConstant.INTERNAL_SERVER_ERROR, ApplicationConstant.INTERNALSERVER_ERROR));
        }
	}

    @Override
    public ResponseEntity<ResponseModel<List<TelegramAdminGroup>>> getAllAdminGroup() {
        try {
 //           logger.info("Fetching all admin groups");

            List<TelegramAdminGroup> telegramAdminGroups = telegramAdminGroupRepo.findAll();
 //           logger.info("Total Groups Fetched: {}", telegramAdminGroups.size());

            if (telegramAdminGroups != null && !telegramAdminGroups.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseModel<>(
                                "Data Found",
                                "success",
                                ApplicationConstant.OK,
                                telegramAdminGroups
                        ));
            } else {
 //               logger.info("No Admin Groups Found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel<>(
                                "No Admin Groups Found",
                                ApplicationConstant.NOT_FOUND,
                                ApplicationConstant.NOTFOUND,
                                null
                        ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Internal Server Error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(
                            "Internal Server Error",
                            ApplicationConstant.INTERNAL_SERVER_ERROR,
                            ApplicationConstant.INTERNALSERVER_ERROR,
                            null
                    ));
        }
    }


	@Override
	public List<TelegramAdminGroup> getAllAdminGroupId() {
		try 
		{			
//			logger.info("Fetch All Admin Group");
			List<TelegramAdminGroup> telegramAdminGroups=telegramAdminGroupRepo.findAll();
			if(telegramAdminGroups!=null)
			{
	            return telegramAdminGroups;
			}
			else 
			{
				return null;
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
            logger.error("Internal Server Error "+e);
            return null;
		}
	}

    @Override
    public ResponseEntity<ResponseModel<TelegramMerchantGroup>> fetchMerchantGroupByMid(String mid) {

        try {
            TelegramMerchantGroup telegramMerchantGroup = telegramGroupRepo.findByMid(mid);
            if (telegramMerchantGroup != null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseModel<>(
                                "Data Found",
                                "success",
                                ApplicationConstant.OK,
                                telegramMerchantGroup
                        ));
            } else {
                logger.warn("No Admin Groups Found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel<>(
                                "No Groups Found",
                                ApplicationConstant.NOT_FOUND,
                                ApplicationConstant.NOTFOUND,
                                null
                        ));

            }
        }
        catch(Exception e)
            {
                logger.error("Internal Server Error", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseModel<>(
                                "Internal Server Error",
                                ApplicationConstant.INTERNAL_SERVER_ERROR,
                                ApplicationConstant.INTERNALSERVER_ERROR,
                                null
                        ));

            }
        }

    @Override
    public ResponseEntity<ResponseModel> editmerchantGroup(String mid, MerchantGroupEditRequestDto group) {
        try {
            if (isNotBlank(group.getEditGroupChatId()) &&
                    isNotBlank(group.getEditGroupName()) &&
                    isNotBlank(group.getEditCompanyName()) &&
                    isNotBlank(group.getEditGroupType()))
             {
                 TelegramMerchantGroup telegramMerchantGroup = telegramGroupRepo.findByMid(mid);

                 if (telegramMerchantGroup != null) {
                     telegramMerchantGroup.setGroupName(group.getEditGroupName());
                     telegramMerchantGroup.setGroupType(group.getEditGroupType());
                     telegramMerchantGroup.setGroupChatId(group.getEditGroupChatId());
                     telegramMerchantGroup.setCompanyName(group.getEditCompanyName());

                     telegramGroupRepo.save(telegramMerchantGroup);

                     return ResponseEntity.status(HttpStatus.OK)
                             .body(new ResponseModel<>(
                                     "Group Updated Successfully",
                                     "success",
                                     ApplicationConstant.OK,
                                     telegramMerchantGroup
                             ));
                 } else {
                     logger.warn("No Groups Found for MID: {}", mid);
                     return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(new ResponseModel<>(
                                     "No Groups Found",
                                     ApplicationConstant.NOT_FOUND,
                                     ApplicationConstant.NOTFOUND,
                                     null
                             ));
                 }
            }
            else
            {
                logger.warn("Bad Request");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseModel<>(
                                "bad request",
                                ApplicationConstant.BAD_REQUEST,
                                HttpStatus.BAD_REQUEST.value(),
                                null
                        ));
            }
        } catch (Exception e) {
            logger.error("Internal Server Error while updating group for MID: {}", mid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(
                            "Internal Server Error",
                            ApplicationConstant.INTERNAL_SERVER_ERROR,
                            ApplicationConstant.INTERNALSERVER_ERROR,
                            null
                    ));
        }
    }


}
