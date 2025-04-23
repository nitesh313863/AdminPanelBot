package com.lincpay.chatbot.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseModel<T> {

    private String message;
    private String status;
    private int statusCode;
    private Object data; 

    public ResponseModel(T data, String message, String status, int statusCode) {
        this.data = data;
        this.message = message;
        this.status = status;
        this.statusCode = statusCode;
    }

    public ResponseModel( String message, String status, int statusCode) {
        this.message = message;
        this.status = status;
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "ResponseModel [message=" + message + ", status=" + status + ", statusCode=" + statusCode + ", data="
                + data + "]";
    }


}