package com.lincpay.chatbot.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@ToString
@Setter
@Getter
public class ExpoToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String expoToken;
    private LocalDateTime createdAt = LocalDateTime.now();
}
