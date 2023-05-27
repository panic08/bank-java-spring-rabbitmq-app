package ru.panic.template.dto;

import lombok.Data;

@Data
public class AuthorizeResponseDto {
    private Integer status;
    private String username;
    private String jwtToken;
}
