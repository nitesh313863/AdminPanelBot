package com.lincpay.chatbot.serviceimp;

import com.lincpay.chatbot.dto.Request.UserLoginRequestDto;
import com.lincpay.chatbot.dto.Request.UserRequestDto;
import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.TelegramMerchantGroup;
import com.lincpay.chatbot.entities.User;
import com.lincpay.chatbot.repository.TelegramMerchantGroupRepo;
import com.lincpay.chatbot.repository.UserRepo;
import com.lincpay.chatbot.serivce.UserService;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImp implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

    @Autowired
    TelegramMerchantGroupRepo telegramMerchantGroupRepo;

    @Autowired
    UserRepo userRepo;

    @Override
    public ResponseEntity<ResponseModel> addUser(UserRequestDto dto) {
        try {
            // Create a new User entity
            User user = new User();
            user.setUsername(dto.getUsername());
            user.setPassword(dto.getPassword()); // Ideally hash password before saving
            user.setEmail(dto.getEmail());
            user.setPhone(dto.getPhone());
            // Fetch all allowed groups from DB by ID
            Set<TelegramMerchantGroup> allowedGroups = new HashSet<>(
                    telegramMerchantGroupRepo.findAllById(dto.getAllowedGroupIds())
            );

            // Set groups to user
            user.setAllowedGroups(allowedGroups);

            // Save user (will auto-populate join table)
            userRepo.save(user);


            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseModel<>("created","success",HttpStatus.CREATED.value()));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error creating user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel("error","internal_server_error",HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @Override
    public ResponseEntity<ResponseModel<List<TelegramMerchantGroup>>> getAllowedGroupsByUsername(String username) {
        try {
            Optional<User> optionalUser = userRepo.findByUsername(username);

            if (!optionalUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel<>("User not found", "not_found", HttpStatus.NOT_FOUND.value()));
            }

            User user = optionalUser.get();
            Set<TelegramMerchantGroup> allowedGroups = user.getAllowedGroups();

            // Convert Set to List (optional, for consistent JSON array)
            List<TelegramMerchantGroup> groupList = new ArrayList<>(allowedGroups);

            return ResponseEntity.ok(
                    new ResponseModel<>("Found Data", "success", HttpStatus.OK.value(),groupList)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>("error", "internal_server_error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
    @Override
    public ResponseEntity<ResponseModel> validUserData(UserLoginRequestDto dto) {
        try {
            Optional<User> optionalUser = userRepo.findByUsername(dto.getUsername());

            if (!optionalUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseModel<>("Invalid username", null, HttpStatus.UNAUTHORIZED.value()));
            }

            User user = optionalUser.get();

            // Ideally, you should hash passwords and compare hashes
            if (!user.getPassword().equals(dto.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseModel<>("Invalid password", null, HttpStatus.UNAUTHORIZED.value()));

            }

            return ResponseEntity.ok(new ResponseModel<>("Login successful", "success", HttpStatus.OK.value(),optionalUser.get().getUsername()));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error validating user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>("Internal server error", "internal_server_error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }



}
