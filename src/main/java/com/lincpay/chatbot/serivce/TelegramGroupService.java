package com.lincpay.chatbot.serivce;

import java.util.List;

import com.lincpay.chatbot.dto.Request.MerchantGroupEditRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.lincpay.chatbot.dto.Request.SendAlertMeassegeInMidGroup;
import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.TelegramAdminGroup;
import com.lincpay.chatbot.entities.TelegramMerchantGroup;

import javax.validation.Valid;

@Service
public interface TelegramGroupService {

	ResponseEntity<ResponseModel> addMerchantGroup(TelegramMerchantGroup telegramGroup);
	public ResponseEntity<ResponseModel<List<TelegramMerchantGroup>>> getAllMerchantGroup();
	public List<TelegramMerchantGroup> getAllGroupForBarodCast();
    void messageStoreInDb(String chatId, String receivedText, String firstName, String lastName);
	TelegramMerchantGroup getMerchantGroupByMid(SendAlertMeassegeInMidGroup sendAlertMeassegeInMidGroup);
	ResponseEntity<ResponseModel> addAdminGroupData(TelegramAdminGroup telegramAdminGroup);
	ResponseEntity<ResponseModel<List<TelegramAdminGroup>>> getAllAdminGroup();
	List<TelegramAdminGroup> getAllAdminGroupId();

	ResponseEntity<ResponseModel<TelegramMerchantGroup>> fetchMerchantGroupByMid(String mid);

	ResponseEntity<ResponseModel> editmerchantGroup(String mid, @Valid MerchantGroupEditRequestDto group);

	List<TelegramMerchantGroup> getAllMerchantGroups();
	public TelegramMerchantGroup getMerchantGroupChatIdByMid(String mid);
}
