package com.lincpay.chatbot.serivce;

import java.util.List;
import org.springframework.http.ResponseEntity;

import com.lincpay.chatbot.dto.Request.PrivilegeRequestDTO;
import com.lincpay.chatbot.dto.response.FetchResponseAdminGroupMember;
import com.lincpay.chatbot.dto.response.GroupMemberResponseDTO;
import com.lincpay.chatbot.dto.response.GroupResponseDTO;
import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.Group;
import com.lincpay.chatbot.entities.GroupMember;

@org.springframework.stereotype.Service
public interface GroupMemberService {

	ResponseEntity<ResponseModel<List<GroupMemberResponseDTO>>> getGroupMemberByGroupId(Long groupId,String mid);

	ResponseEntity<ResponseModel<List<FetchResponseAdminGroupMember>>> getGroupAdmins(Long groupId);

	ResponseEntity<ResponseModel<List<GroupResponseDTO>>> fetchAllGroupName();

	ResponseEntity<ResponseModel<List<GroupMemberResponseDTO>>> fetchAllGroupMember(Long groupid);

	ResponseEntity<ResponseModel> updateGroupMemberPrivileges(Long groupId, List<PrivilegeRequestDTO> privileges);
	
	boolean getGroupMemberByGroupIdAndUserId(String chatId, Long userId);

	ResponseEntity<ResponseModel> deleteGroup(Long groupId);

	ResponseEntity<ResponseModel<List<GroupMemberResponseDTO>>> getPrivilageMemberByGroupId(Long groupId);


}
