package com.lincpay.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lincpay.chatbot.entities.Group;


@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findByGroupId(Long inputValue); // Fetch group by groupId
    
}
