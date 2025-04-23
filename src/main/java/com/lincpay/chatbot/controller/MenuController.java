package com.lincpay.chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.AddMenuTelegramAdminPanel;
import com.lincpay.chatbot.serivce.AddMenuService;

@RestController
@CrossOrigin("*")
@RequestMapping("/telegram-menu")
public class MenuController {
	
	@Autowired
	AddMenuService addMenuService;
	
  @PostMapping("/addMenu")
  public ResponseEntity<ResponseModel> addMenus(@RequestBody AddMenuTelegramAdminPanel addMenuTelegram)
  {
      ResponseEntity<ResponseModel> response=addMenuService.addMenue(addMenuTelegram);
      return response;
  }

}
