package ru.panic.template.dto;

import lombok.Data;
@Data
public class PreWithdrawalRequestDto {
    private String orderId;
    private String username;
    private Integer code;
}
