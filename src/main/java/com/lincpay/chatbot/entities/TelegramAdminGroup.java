package com.lincpay.chatbot.entities;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class TelegramAdminGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String groupName;

    @Column(nullable = false, unique = true)
    private String groupChatId;

    private String groupDescription;

    @Column(nullable = false)
    private String groupType;

    @Column(nullable = false)
    private Boolean isActive = true;
    
    private String createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    private Long lastModifiedBy;
    
    private LocalDateTime lastModifiedAt;

    // ✅ Automatically set createdAt and updatedAt before saving a new entity
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // ✅ Automatically update updatedAt before updating an existing entity
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
