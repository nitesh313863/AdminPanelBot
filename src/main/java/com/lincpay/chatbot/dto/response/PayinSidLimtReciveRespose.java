package com.lincpay.chatbot.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PayinSidLimtReciveRespose {
	private Long id;

	private String company;

	private String sid;

	private String maxTxnAmount;

	private String mid;

	private String remainingLimit;
	
	private String createDateTime;
	
	private String updatedDateTime;
	
	private boolean isDeleted;
	
	
	private Long vehicleMasterId;
	private String vehicleName;
	private  Long masterMarchantid;
	private String masterMarchantName;
	private String merchantVpa;
	private String prifix;
	
	
	private String domain;
	private String processor;
}
