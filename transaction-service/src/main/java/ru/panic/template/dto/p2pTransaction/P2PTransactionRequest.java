package ru.panic.template.dto.p2pTransaction;

import lombok.Data;
import ru.panic.template.dto.enums.Currency;

@Data
public class P2PTransactionRequest {
    private String from;
    private String to;
    private Currency currency;
    private Double amount;
    private String desc;
    private String sign;
}
