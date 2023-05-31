package ru.panic.template.dto.p2pTransaction;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import ru.panic.template.dto.enums.Currency;

@Data
public class P2PTransactionResponse {
    private Integer status;
    private String orderId;
    private String from;
    private String to;
    private Currency currency;
    private Double amount;
    private String desc;
    private Long timestamp;
    private String sign;
}
