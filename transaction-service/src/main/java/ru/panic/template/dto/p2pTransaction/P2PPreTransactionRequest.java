package ru.panic.template.dto.p2pTransaction;

import lombok.Data;

@Data
public class P2PPreTransactionRequest {
    private String orderId;
    private String username;
    private Integer code;
}
