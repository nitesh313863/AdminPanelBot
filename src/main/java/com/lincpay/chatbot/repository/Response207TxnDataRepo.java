package com.lincpay.chatbot.repository;


import com.lincpay.chatbot.entities.Response207TxnData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface Response207TxnDataRepo extends JpaRepository<Response207TxnData, Long> {
    @Query(value = "SELECT r.id, r.txn_id, r.mid, r.txn_date, r.txn_status, r.response_code, r.created_at, " +
            "p.amount, p.order_no " +
            "FROM response_207_txn_data r " +
            "JOIN payment_master_table p ON r.txn_id = p.txn_id " +
            "WHERE r.created_at BETWEEN :startTime AND :endTime", nativeQuery = true)
    List<Object[]> findTxnDetailsBetween(@Param("startTime") Date start, @Param("endTime") Date end);

}
