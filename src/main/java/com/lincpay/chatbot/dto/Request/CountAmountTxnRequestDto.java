package com.lincpay.chatbot.dto.Request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CountAmountTxnRequestDto {

    private int midTotalCount;
    private long totalTxnCount;
    private BigDecimal totalAmount;
    private List<MidSummaryDTO> midSummaries;

    // Getters and Setters
    public int getMidTotalCount() {
        return midTotalCount;
    }

    public void setMidTotalCount(int midTotalCount) {
        this.midTotalCount = midTotalCount;
    }

    public long getTotalTxnCount() {
        return totalTxnCount;
    }

    public void setTotalTxnCount(long totalTxnCount) {
        this.totalTxnCount = totalTxnCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<MidSummaryDTO> getMidSummaries() {
        return midSummaries;
    }

    public void setMidSummaries(List<MidSummaryDTO> midSummaries) {
        this.midSummaries = midSummaries;
    }

    // ✅ Inner class for MID summary
    public static class MidSummaryDTO {
        private String mid;
        private long txnCount;
        private BigDecimal totalAmount;

        // Getters and Setters
        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public long getTxnCount() {
            return txnCount;
        }

        public void setTxnCount(long txnCount) {
            this.txnCount = txnCount;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }
    }

}
