package ru.panic.template.dto;

import lombok.Data;

@Data
public class AuthorizeResponseDto {
    private String username;
    private String jwtToken;
}
