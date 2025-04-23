package com.lincpay.chatbot.entities;

import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "response_207_txn_data")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response207TxnData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "txn_id", unique = true, nullable = false)
    private String txnId;

    @Column(name = "mid")
    private String mid;

    @Column(name = "txn_date")
    private Date txnDate;

    @Column(name = "txn_status")
    private String txnStatus;

    @Column(name = "response_code")
    private String responseCode;

    @Column(name = "created_at")
    private Date createdAt;
}

