package ru.panic.template.dto.p2pTransaction;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class P2PPreTransactionResponse {
    private Integer status;
    private String orderId;
    private String username;
}
