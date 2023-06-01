package ru.panic.template.dto;

import lombok.Data;
import ru.panic.template.dto.enums.Currency;
import ru.panic.template.dto.enums.Method;

@Data
public class WithdrawalRequestDto {
    private Integer status;
    private String orderId;
    private String from;
    private String to;
    private Currency currency;
    private Method method;
    private Double amount;
    private Long timestamp;
    private String sign;
}
