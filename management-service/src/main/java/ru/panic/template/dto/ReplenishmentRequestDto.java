package ru.panic.template.dto;

import lombok.Data;
import ru.panic.template.dto.enums.Currency;
import ru.panic.template.dto.enums.Method;

@Data
public class ReplenishmentRequestDto {
    private Integer status;
    private String username;
    private Double amount;
    private Currency currency;
    private Method method;
    private Long timestamp;
}
