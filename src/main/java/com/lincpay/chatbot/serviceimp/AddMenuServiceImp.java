package com.lincpay.chatbot.serviceimp;

import com.lincpay.chatbot.dto.response.ResponseModel;
import com.lincpay.chatbot.entities.AddMenuTelegramAdminPanel;
import com.lincpay.chatbot.repository.AddMenuTelegramRepo;
import com.lincpay.chatbot.serivce.AddMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class AddMenuServiceImp implements AddMenuService {

    //  Logger for tracking actions and errors
    private static final Logger logger = LoggerFactory.getLogger(AddMenuServiceImp.class);

    @Autowired
    AddMenuTelegramRepo addMenuTelegramRepo;

    /**
     *  Add new menu item for Telegram Admin Panel.
     * 
     * @param addMenuTelegram AddMenuTelegramAdminPanel object with menu name and description.
     * @return ResponseEntity with ResponseModel indicating success or error status.
     */
    @Override
    public ResponseEntity<ResponseModel> addMenue(AddMenuTelegramAdminPanel addMenuTelegram) {
        try {
            logger.info("Received request to add menu: {}", addMenuTelegram.getMenuName());

            //  Validate menu name and description
            if (addMenuTelegram.getMenuName() == null || addMenuTelegram.getMenuDescription() == null) {
                logger.warn("Invalid input - menuName or menuDescription is null.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseModel("All input Required", "Invalid Input", HttpStatus.BAD_REQUEST.value()));
            }

            //  Save menu to database
            AddMenuTelegramAdminPanel savedMenu = addMenuTelegramRepo.save(addMenuTelegram);

            //  Check if the menu is saved successfully
            if (savedMenu != null) {
                logger.info("Menu '{}' added successfully with ID: {}", savedMenu.getMenuName(), savedMenu.getId());
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseModel("Created", "Success", HttpStatus.CREATED.value()));
            } else {
                logger.error("Failed to save menu. Internal server error.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseModel("Some Error Occurred", "Internal Server Error",
                                HttpStatus.INTERNAL_SERVER_ERROR.value()));
            }
        } catch (Exception e) {
            logger.error("Error occurred while adding menu: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel("Error", "Exception Occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     *  Find and return all available menu items from the database.
     * 
     * @return List of AddMenuTelegramAdminPanel objects.
     */
    public List<AddMenuTelegramAdminPanel> findAllMenuAdmin() {
        try {
          //  logger.info("Fetching all available menus...");
            List<AddMenuTelegramAdminPanel> menuList = addMenuTelegramRepo.findAll();

            if (menuList.isEmpty()) {
                logger.warn("No menus found in the database.");
            } else {
       //         logger.info("{} menus found and returned successfully.", menuList.size());
            }

            return menuList;
        } catch (Exception e) {
            logger.error("Error occurred while fetching menus: {}", e.getMessage(), e);
            return null;
        }
    }
}
