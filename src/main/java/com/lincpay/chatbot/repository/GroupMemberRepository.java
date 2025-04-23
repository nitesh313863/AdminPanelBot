	package com.lincpay.chatbot.repository;
	
	import java.util.List;
	import java.util.Optional;
	
	import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
	
	import com.lincpay.chatbot.entities.Group;
	import com.lincpay.chatbot.entities.GroupMember;
	
	@Repository
	public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
	
	
		List<GroupMember> findByGroup(Group group);
		
	    Optional<GroupMember> findByUserIdAndGroup(Long userId, Group group);
	
	    List<GroupMember> findByGroup_GroupId(Long groupId);
	
	    Optional<GroupMember> findByGroup_GroupIdAndUserId(Long groupId, Long userId);

	    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.groupId = :groupId AND gm.privilege = 'Admin'")
	    List<GroupMember> findGroupMemberPrivilage(@Param("groupId") Long groupId);

	
	
	}
