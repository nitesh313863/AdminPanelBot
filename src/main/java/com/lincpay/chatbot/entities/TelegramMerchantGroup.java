package com.lincpay.chatbot.entities;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class TelegramMerchantGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ✅ Group name (display name of the merchant group)
    @Column(nullable = false, unique = true)
    private String groupName;

    // ✅ Telegram group chat ID (unique identifier from Telegram)
    @Column(nullable = false, unique = true)
    private String groupChatId;


    private String mid;

    // ✅ Merchant company name (for displaying in list format)
    private String companyName;

    // ✅ Group type: public, private, supergroup
    @Column(nullable = false)
    private String groupType;

    // ✅ Status to check if the group is active or inactive
    @Column(nullable = false)
    private Boolean isActive = true;

    // ✅ Created timestamp (auto-managed with @PrePersist)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ✅ Updated timestamp (auto-managed with @PreUpdate)
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ✅ Admin/creator ID who added the merchant group
    private String createdBy;

    // ✅ Last modified by which admin (optional for audit)
    private Long lastModifiedBy;

    // ✅ Last modified timestamp
    private LocalDateTime lastModifiedAt;

    // ✅ Automatically set createdAt and updatedAt before saving a new entity
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // ✅ Automatically update updatedAt before modifying an entity
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
