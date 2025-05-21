package com.lincpay.chatbot.repository;

import com.lincpay.chatbot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);


    @Query("SELECT u.id FROM User u JOIN u.allowedGroups g WHERE g.id = :groupId")
    List<Long> findUserIdsByGroupId(@Param("groupId") Integer groupId);

}
