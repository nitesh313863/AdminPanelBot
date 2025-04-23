package com.lincpay.chatbot.entities;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRequestAction {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "withdrawal_id", nullable = false)
    private Integer withdrawalId;

    @Column(name = "approved_by")
    private String approvedBy;  // Stores who approved

    @Column(name = "approved_by_id")
    private Long approvedById;  // Stores Telegram userId of approver

    @Column(name = "rejected_by")
    private String rejectedBy;  // Stores who rejected

    @Column(name = "rejected_by_id")
    private Long rejectedById;  // Stores Telegram userId of rejector

    @Column(name = "status", nullable = false)
    private String status;  // "approved" or "rejected"

    @Column(name = "transaction_date")
    private String transactionDate;

    @Column(name = "utr")
    private String utr;

    @Column(name = "remark")
    private String remark;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
