package ru.panic.template.dto;

import lombok.Data;

@Data
public class ProviderResponseDto {
    private String username;
    private Number rub_balance;
    private Number usd_balance;
    private Number eur_balance;
    private String jwtToken;
}
