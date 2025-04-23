package com.lincpay.chatbot.entities;

import lombok.*;
import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AddMenuTelegramAdminPanel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String menuName;
    private String menuDescription;
}
