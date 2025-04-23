package com.lincpay.chatbot.dto.response;

import com.lincpay.chatbot.dto.Request.Notification10FailedTxnRequestDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Notification10FailedTxnRequestDtoMapper implements RowMapper<Notification10FailedTxnRequestDto> {
    @Override
    public Notification10FailedTxnRequestDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        Notification10FailedTxnRequestDto dto = new Notification10FailedTxnRequestDto();
        dto.setMid(rs.getString("mid"));
        dto.setMsg(rs.getString("alert_msg"));  // Ensuring correct mapping from alert_msg column
        dto.setDate(rs.getTimestamp("created_at"));  // Ensuring correct mapping from created_at column
        return dto;
    }
}
