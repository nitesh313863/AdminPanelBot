package com.lincpay.chatbot.dto.Request;

import lombok.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MerchantGroupEditRequestDto {

    @NotBlank(message = "Group Name must not be blank")
    private String editGroupName;

    @NotBlank(message = "Group Type must not be blank")
    private String editGroupType;

    @NotBlank(message = "Company Name must not be blank")
    private String editCompanyName;

    @NotBlank(message = "Group Chat ID must not be blank")
    private String editGroupChatId;
}
