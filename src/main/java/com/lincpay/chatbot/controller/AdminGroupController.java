package com.lincpay.chatbot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.TelegramAdminGroup;
import com.lincpay.chatbot.serivce.TelegramGroupService;

@RestController
@CrossOrigin("*")
@RequestMapping("/admin-group")
public class AdminGroupController {

    private final TelegramGroupService telegramGroupService;

    public AdminGroupController(TelegramGroupService telegramGroupService) {
        this.telegramGroupService = telegramGroupService;
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseModel> addAdminGroup(@RequestBody TelegramAdminGroup adminGroup) {
        return telegramGroupService.addAdminGroupData(adminGroup);
    }

    @GetMapping("/getAll")
    public ResponseEntity<ResponseModel<List<TelegramAdminGroup>>> getAllAdminGroups() {
        return telegramGroupService.getAllAdminGroup();
    }
}

