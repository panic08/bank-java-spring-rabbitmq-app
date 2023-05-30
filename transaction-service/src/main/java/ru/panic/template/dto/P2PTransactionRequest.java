package ru.panic.template.dto;

import lombok.Data;
import ru.panic.template.dto.enums.Currency;

@Data
public class P2PTransactionRequest {
    private String orderId;
    private String from;
    private String to;
    private Currency currency;
    private Double amount;
    private String desc;
    private Integer code;
    private String sign;
}
