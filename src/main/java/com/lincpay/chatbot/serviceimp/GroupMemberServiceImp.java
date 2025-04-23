package com.lincpay.chatbot.serviceimp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lincpay.chatbot.dto.Request.PrivilegeRequestDTO;
import com.lincpay.chatbot.dto.response.FetchResponseAdminGroupMember;
import com.lincpay.chatbot.dto.response.GroupMemberResponseDTO;
import com.lincpay.chatbot.dto.response.GroupResponseDTO;
import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.Group;
import com.lincpay.chatbot.entities.GroupMember;
import com.lincpay.chatbot.repository.GroupMemberRepository;
import com.lincpay.chatbot.repository.GroupRepository;
import com.lincpay.chatbot.serivce.GroupMemberService;


@Service
public class GroupMemberServiceImp implements GroupMemberService {
	@Value("${telegram.bot.token}")
	private String botToken;
	
	private static final Logger logger = LoggerFactory.getLogger(GroupMemberServiceImp.class);
	
	@Autowired
	GroupMemberRepository groupMemberRepository;
	
	
	@Autowired
	GroupRepository groupRepository;

		@Override
		public ResponseEntity<ResponseModel<List<GroupMemberResponseDTO>>> getGroupMemberByGroupId(Long inputValue, String mid) {
			try {
				// Extract script from resources before execution
				String pythonScriptPath = extractPythonScript("scripts/get_members.py");

				// Execute the Python script
				ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, inputValue.toString());
				processBuilder.redirectErrorStream(true);
				Process process = processBuilder.start();

				// Read script output
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				StringBuilder output = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					output.append(line);
				}

				int exitCode = process.waitFor();
				if (exitCode != 0) {
					return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
							.body(new ResponseModel<>("Error", "Python script execution failed", HttpStatus.SC_INTERNAL_SERVER_ERROR, null));
				}

				// Parse JSON response
				JSONObject responseJson = new JSONObject(output.toString());
				String groupName = responseJson.optString("group_name", "Unknown Group");
				int totalMembers = responseJson.optInt("total_members", 0);

				JSONArray membersArray = responseJson.optJSONArray("members");
				if (membersArray == null) {
					membersArray = new JSONArray(); // Default to empty array
				}

				List<GroupMemberResponseDTO> memberList = new ArrayList<>();
				Set<Long> newMemberUserIds = new HashSet<>();
				for (int i = 0; i < membersArray.length(); i++) {
					JSONObject memberJson = membersArray.getJSONObject(i);
					long userId = memberJson.optLong("user_id", 0);
					String firstName = memberJson.optString("first_name", null);
					String lastName = memberJson.optString("last_name", null);
					String username = memberJson.optString("username", null);
					boolean admin = memberJson.optBoolean("admin", false);

					memberList.add(new GroupMemberResponseDTO(userId, firstName, lastName, username, null));
					newMemberUserIds.add(userId);
				}

				// Fetch or create group in database
				Group group = groupRepository.findByGroupId(inputValue);
				if (group == null) {
					group = new Group();
					group.setGroupId(inputValue);
					group.setGroupName(groupName);
					group.setCreatedDate(LocalDateTime.now());
					group.setMid(mid);
					group.setTotalMembers(totalMembers);
					group = groupRepository.save(group);
				} else {
					group.setGroupName(groupName);
					group.setTotalMembers(totalMembers);
					group = groupRepository.save(group);
				}

				// Fetch existing members for this group
				List<GroupMember> existingMembers = groupMemberRepository.findByGroup(group);
				Set<Long> existingMemberUserIds = existingMembers.stream()
						.map(GroupMember::getUserId)
						.collect(Collectors.toSet());

				// Remove members no longer in Telegram group
				for (GroupMember existingMember : existingMembers) {
					if (!newMemberUserIds.contains(existingMember.getUserId())) {
						groupMemberRepository.delete(existingMember);
					}
				}

				// Insert or update members
				for (GroupMemberResponseDTO memberResponse : memberList) {
					Optional<GroupMember> existingMemberOpt = groupMemberRepository.findByUserIdAndGroup(memberResponse.getUserId(), group);

					if (existingMemberOpt.isPresent()) {
						GroupMember existingMember = existingMemberOpt.get();
						boolean updated = false;

						if (!Objects.equals(existingMember.getFirstName(), memberResponse.getFirstName())) {
							existingMember.setFirstName(memberResponse.getFirstName());
							updated = true;
						}
						if (!Objects.equals(existingMember.getLastName(), memberResponse.getLastName())) {
							existingMember.setLastName(memberResponse.getLastName());
							updated = true;
						}
						if (!Objects.equals(existingMember.getUsername(), memberResponse.getUsername())) {
							existingMember.setUsername(memberResponse.getUsername());
							updated = true;
						}

						if (updated) {
							groupMemberRepository.save(existingMember);  // Save only if changes exist
						}
					} else {
						// Insert new member
						GroupMember newMember = new GroupMember();
						newMember.setUserId(memberResponse.getUserId());
						newMember.setFirstName(memberResponse.getFirstName());
						newMember.setLastName(memberResponse.getLastName());
						newMember.setUsername(memberResponse.getUsername());
						newMember.setGroup(group);

						groupMemberRepository.save(newMember);
					}
				}

				// Return response
				return ResponseEntity.status(HttpStatus.SC_OK)
						.body(new ResponseModel<>("Data Fetch", "success", HttpStatus.SC_OK, memberList));

			} catch (Exception e) {
				logger.error("Failed to run Python script", e);
				return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
						.body(new ResponseModel<>("Error", "Internal Server Error", HttpStatus.SC_INTERNAL_SERVER_ERROR, null));
			}
		}

		private String extractPythonScript(String resourcePath) throws IOException {
			// Load the script from classpath
			ClassPathResource resource = new ClassPathResource(resourcePath);

			if (!resource.exists()) {
				throw new FileNotFoundException("Python script not found in resources!");
			}

			// Create a temp file
			File tempScript = File.createTempFile("get_members", ".py");
			tempScript.deleteOnExit();

			// Copy script content to temp file
			try (InputStream inputStream = resource.getInputStream();
				 FileOutputStream outputStream = new FileOutputStream(tempScript)) {
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			}

			return tempScript.getAbsolutePath(); // Return the temp file path
		}




	public ResponseEntity<ResponseModel<List<FetchResponseAdminGroupMember>>> getGroupAdmins(Long groupId) {
	    String apiUrl = "https://api.telegram.org/bot" + botToken + "/getChatAdministrators?chat_id=" + groupId;
	    RestTemplate restTemplate = new RestTemplate();

	    ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

	    if (response.getStatusCode().is2xxSuccessful()) {
	        try {
	            JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
	            
	            if (jsonNode.get("ok").asBoolean()) {
	                List<FetchResponseAdminGroupMember> adminList = new ArrayList<>();
	                
	                for (JsonNode admin : jsonNode.get("result")) {
	                    JsonNode userNode = admin.get("user");

	                    FetchResponseAdminGroupMember groupAdminDTO = new FetchResponseAdminGroupMember();
	                    groupAdminDTO.setId(userNode.get("id").asLong());
	                    groupAdminDTO.setFirstName(userNode.has("first_name") ? userNode.get("first_name").asText() : "Unknown");
	                    groupAdminDTO.setLastName(userNode.has("last_name") ? userNode.get("last_name").asText() : "");
	                    groupAdminDTO.setUsername(userNode.has("username") && !userNode.get("username").isNull()
	                            ? userNode.get("username").asText()
	                            : "N/A");
	                    groupAdminDTO.setStatus(admin.has("status") ? admin.get("status").asText() : "unknown");
	                    groupAdminDTO.setIsBot(userNode.get("is_bot").asBoolean());

	                    adminList.add(groupAdminDTO);
	                }

	                // Return successful response
	                ResponseModel<List<FetchResponseAdminGroupMember>> responseModel =
	                        new ResponseModel<>("Data fetched successfully", "success", HttpStatus.SC_OK, adminList);

	                return ResponseEntity.ok(responseModel);
	            }
	        } catch (JsonProcessingException e) {
	            logger.error("Error in Fetching Group Admin Member");
	        }
	    }

	    // Return error response if something goes wrong
	    ResponseModel<List<FetchResponseAdminGroupMember>> errorResponse =
	            new ResponseModel<>("Failed to fetch data", "error", HttpStatus.SC_INTERNAL_SERVER_ERROR, null);

	    return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(errorResponse);
	}


	@Override
	public ResponseEntity<ResponseModel<List<GroupResponseDTO>>> 	fetchAllGroupName() {
	    ResponseModel<List<GroupResponseDTO>> response = new ResponseModel<>();
	    
	    try {
	        List<GroupResponseDTO> groupDTOs = groupRepository.findAll().stream()
	            .map(group -> new GroupResponseDTO(
	                group.getId(),
	                group.getGroupName(),
	                group.getGroupId(),
	                group.getCreatedDate(),
	                group.getMid(),
	                group.getTotalMembers()
	            ))
	            .collect(Collectors.toList());

	        if (groupDTOs.isEmpty()) {
	            response.setData(null);
	            response.setMessage("No groups found");
	            response.setStatus("not found");
	            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
	            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(response);
	        }

	        response.setData(groupDTOs);
	        response.setMessage("Groups fetched successfully");
	        response.setStatus("success");
	        response.setStatusCode(HttpStatus.SC_OK);
	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        response.setData(null);
	        response.setMessage("An internal server error occurred: " + e.getMessage());
	        response.setStatus("enternal server error");
	        response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
	        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(response);
	    }
	}

	public ResponseEntity<ResponseModel<List<GroupMemberResponseDTO>>> fetchAllGroupMember(Long groupId) {
	    ResponseModel<List<GroupMemberResponseDTO>> response = new ResponseModel<>();

	    try {
	        // Validate input
	        if (groupId == null) {
	            response.setMessage("Group ID cannot be null");
	            response.setStatus("BAD_REQUEST");
	            return ResponseEntity.badRequest().body(response);
	        }

	        List<GroupMember> members = groupMemberRepository.findByGroup_GroupId(groupId);

	        if (members.isEmpty()) {
	            response.setMessage("No group members found for the given Group ID");
	            response.setStatus("NOT_FOUND");
	            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
	            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(response);
	        }

	        // Convert to DTO
	        List<GroupMemberResponseDTO> memberDTOs = members.stream()
	            .map(this::convertToDto)
	            .collect(Collectors.toList());

	        response.setData(memberDTOs);
	        response.setMessage("Group members fetched successfully");
	        response.setStatus("SUCCESS");
	        response.setStatusCode(HttpStatus.SC_OK);
	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        logger.error("Error fetching group members for group ID: " + groupId, e);
	        response.setMessage("An internal server error occurred");
	        response.setStatus("ERROR");
	        return ResponseEntity.internalServerError().body(response);
	    }
	}

	private GroupMemberResponseDTO convertToDto(GroupMember member) {
	    GroupMemberResponseDTO dto = new GroupMemberResponseDTO();
	    dto.setUserId(member.getUserId());
	    dto.setFirstName(member.getFirstName());
	    dto.setLastName(member.getLastName());
	    dto.setUsername(member.getUsername());
	    dto.setPrivilege(member.getPrivilege());    
	    return dto;
	}



	@Override
	public ResponseEntity<ResponseModel> updateGroupMemberPrivileges(Long groupId,
			List<PrivilegeRequestDTO> privileges) {
	    try {
	        for (PrivilegeRequestDTO privilegeRequest : privileges) {
	            Long userId = privilegeRequest.getUserId(); // Convert userId to Long

	            // Find the group member by groupId and userId
	            Optional<GroupMember> optionalGroupMember = groupMemberRepository.findByGroup_GroupIdAndUserId(groupId, userId);

	            if (optionalGroupMember.isPresent()) {
	                GroupMember groupMember = optionalGroupMember.get();
	                
	                // Update privilege (You need to define how privilege is stored in GroupMember)
	                groupMember.setPrivilege(privilegeRequest.getPrivilege());

	                // Save updated group member
	                groupMemberRepository.save(groupMember);
	            } else {
	                // Log warning if user not found in the group
	                logger.warn("Group member not found for Group ID: {} and User ID: {}", groupId, userId);
	                return ResponseEntity.ok(new ResponseModel<>("not found", "Group Member Not Found",HttpStatus.SC_NOT_FOUND));
	            }
	        }

	        return ResponseEntity.ok(new ResponseModel<>("success", "Privileges updated successfully",HttpStatus.SC_OK));

	    } catch (Exception e) {
	        logger.error("Error updating group member privileges: {}", e.getMessage());
	        return ResponseEntity.status(500).body(new ResponseModel<>("ERROR", "Server error while updating privileges", HttpStatus.SC_INTERNAL_SERVER_ERROR));
	    }
	}



	public boolean getGroupMemberByGroupIdAndUserId(String charId, Long userId) {
	    Long groupId = Long.parseLong(charId);
	    
	    return groupMemberRepository.findByGroup_GroupIdAndUserId(groupId, userId)
	        .map(groupMember -> "admin".equalsIgnoreCase(groupMember.getPrivilege()))
	        .orElse(false);
	}

    @Transactional
    public ResponseEntity<ResponseModel> deleteGroup(Long groupId) {
        // Find the group by groupId
        Group group = groupRepository.findByGroupId(groupId);
        if(group!=null)
        {
        	 groupRepository.delete(group);
             ResponseModel response = new ResponseModel("Group and all Group members deleted successfully.", "success",HttpStatus.SC_OK);
             return ResponseEntity.ok(response);
        	 
        }
        else 
        {
            ResponseModel response = new ResponseModel("Group Not Found This GroupID", "Not Found",HttpStatus.SC_NOT_FOUND);
            return ResponseEntity.ok(response);
		}

    }

	@Override
	public ResponseEntity<ResponseModel<List<GroupMemberResponseDTO>>> getPrivilageMemberByGroupId(Long groupId) {
		List<GroupMember> groupMembers = groupMemberRepository.findGroupMemberPrivilage(groupId);
		try {
			if (groupMembers != null) {
				List<GroupMemberResponseDTO> memberDTOs = groupMembers.stream()
						.map(this::convertToDto)
						.collect(Collectors.toList());

				ResponseModel response;
				if (memberDTOs != null && !memberDTOs.isEmpty()) {
					response = new ResponseModel("All Group Member", "success", HttpStatus.SC_OK, memberDTOs);
				} else {
					response = new ResponseModel("Group Is Present but Group Member Privilege Not Added", "Not Found", HttpStatus.SC_NOT_FOUND);
				}
				return ResponseEntity.ok(response);
			} else {
				ResponseModel response = new ResponseModel("Group Not Found", "No Found", HttpStatus.SC_OK);
				return ResponseEntity.ok(response);
			}
		} catch (Exception e) {
			ResponseModel response = new ResponseModel("Error", "Internal Server Error", HttpStatus.SC_INTERNAL_SERVER_ERROR);
			return ResponseEntity.ok(response);
		}
	}




}
