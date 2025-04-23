package com.lincpay.chatbot.entities;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "group_table")
public class Group {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name")  // Explicit column mapping
    private String groupName;

    @Column(name = "group_id", unique = true, nullable = false)  // Explicitly define column name
    private Long groupId;   

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "mid")
    private String mid;  

    @Column(name = "total_members")
    private int totalMembers;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> groupMembers; 

}
