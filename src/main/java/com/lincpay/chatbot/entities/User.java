package com.lincpay.chatbot.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "telegram_user") // ✅ Ensure this matches the actual table name in DB
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ✅ Primary key, auto-generated

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    private String phone;

    // ✅ Correct Many-to-Many relationship with join table
    @ManyToMany
    @JoinTable(
            name = "user_group_mapping", // ✅ Join table
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), // FK to this entity
            inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id") // FK to TelegramMerchantGroup
    )
    private Set<TelegramMerchantGroup> allowedGroups = new HashSet<>();
}
