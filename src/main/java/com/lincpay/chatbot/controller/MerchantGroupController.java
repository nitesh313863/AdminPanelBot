package com.lincpay.chatbot.controller;

import java.util.List;

import com.lincpay.chatbot.dto.Request.MerchantGroupEditRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.TelegramMerchantGroup;
import com.lincpay.chatbot.serivce.TelegramGroupService;

import javax.validation.Valid;

@RestController
@CrossOrigin("*")
@RequestMapping("/merchant-group")
public class MerchantGroupController {

    private final TelegramGroupService telegramGroupService;
    
    public MerchantGroupController(TelegramGroupService telegramGroupService) {
        this.telegramGroupService = telegramGroupService;
    }

    @PostMapping("/addMerchantGroup")
    public ResponseEntity<ResponseModel> addMerchantGroup(@RequestBody TelegramMerchantGroup group) {
        return telegramGroupService.addMerchantGroup(group);
    }

    @GetMapping("/getAllMerchantGroup")
    public ResponseEntity<ResponseModel<List<TelegramMerchantGroup>>> getAllMerchantGroups() {
        return telegramGroupService.getAllMerchantGroup();
    }

    @GetMapping("/getMerchantGroupByMid/{mid}")
    public ResponseEntity<ResponseModel<TelegramMerchantGroup>> getMerchantGroupByMid(@PathVariable("mid") String mid) {
        return telegramGroupService.fetchMerchantGroupByMid(mid);
    }
    @PutMapping("editMerchantGroupByMid/{mid}")
    public ResponseEntity<ResponseModel> editMerchantGroupByMid(@PathVariable("mid") String mid,
            @Valid @RequestBody MerchantGroupEditRequestDto group)
    {
        return telegramGroupService.editmerchantGroup(mid,group);
    }

}
