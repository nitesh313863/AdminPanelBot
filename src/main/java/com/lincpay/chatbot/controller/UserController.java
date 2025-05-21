package com.lincpay.chatbot.controller;

import com.lincpay.chatbot.dto.Request.UserLoginRequestDto;
import com.lincpay.chatbot.dto.Request.UserRequestDto;
import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.TelegramMerchantGroup;
import com.lincpay.chatbot.serivce.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/addUser")
    public ResponseEntity<ResponseModel> addUser(@RequestBody UserRequestDto dto) {
        return userService.addUser(dto);
    }

    @GetMapping("groups")
    public ResponseEntity<ResponseModel<List<TelegramMerchantGroup>>> getGroupsForLoggedInUser(@RequestParam String username) {
        return userService.getAllowedGroupsByUsername(username);
    }

    @PostMapping("telegramLogin")
    public ResponseEntity<ResponseModel> telegramLogin(@RequestBody UserLoginRequestDto dto) {
        return userService.validUserData(dto);
    }

}
