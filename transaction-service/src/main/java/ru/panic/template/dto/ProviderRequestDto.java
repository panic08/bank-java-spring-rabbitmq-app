package ru.panic.template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProviderRequestDto {
    private String jwtToken;
}
