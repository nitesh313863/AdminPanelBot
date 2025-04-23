package com.lincpay.chatbot.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.lincpay.chatbot.dto.Request.PrivilegeRequestDTO;
import com.lincpay.chatbot.dto.response.GroupMemberResponseDTO;
import com.lincpay.chatbot.dto.response.GroupResponseDTO;
import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.serivce.GroupMemberService;

@RestController
@CrossOrigin("*")
@RequestMapping("/group-members")
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    public GroupMemberController(GroupMemberService groupMemberService) {
        this.groupMemberService = groupMemberService;
    }

    @PostMapping("/addAllGroupMemberOrGroup")
    public ResponseEntity<ResponseModel<List<GroupMemberResponseDTO>>> addGroupMember(
            @RequestParam("groupId") Long groupId, @RequestParam("mid") String mid) {
        return groupMemberService.getGroupMemberByGroupId(groupId, mid);
    }
    
    @DeleteMapping("/deleteGroup/{groupId}")
    public ResponseEntity<ResponseModel> deleteGroup(@PathVariable Long groupId) {
        return groupMemberService.deleteGroup(groupId);
    }

    @GetMapping("/getAllGroup")
    public ResponseEntity<ResponseModel<List<GroupResponseDTO>>> getAllGroups() {
        return groupMemberService.fetchAllGroupName();
    }

    @GetMapping("/getAllGroupMember")
    public ResponseEntity<ResponseModel<List<GroupMemberResponseDTO>>> getAllGroupMembers(@RequestParam Long groupId) {
        return groupMemberService.fetchAllGroupMember(groupId);
    }
    
    @PostMapping("/update-privileges")
    public ResponseEntity<ResponseModel> updatePrivileges(
            @RequestParam Long groupId, @RequestBody List<PrivilegeRequestDTO> privileges) {
        return groupMemberService.updateGroupMemberPrivileges(groupId, privileges);
    }
    
    @GetMapping("/getAllPrivilageGroupMember/{groupId}")
    public ResponseEntity<ResponseModel<List<GroupMemberResponseDTO>>> getAllPrivilageGroupMember(@PathVariable Long groupId) {
        return groupMemberService.getPrivilageMemberByGroupId(groupId);
    }
//  @GetMapping("/getALLGroupAdminMemberByGroupId")
//  public ResponseEntity<ResponseModel<List<FetchResponseAdminGroupMember>>> getGroupAdmins(@RequestParam Long groupId) {
//  	System.out.println(groupId);
//      return groupMemberService.getGroupAdmins(groupId);
//  }
}

