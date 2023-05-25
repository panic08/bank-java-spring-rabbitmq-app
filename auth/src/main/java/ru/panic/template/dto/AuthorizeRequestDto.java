package ru.panic.template.dto;

import lombok.Data;

@Data
public class AuthorizeRequestDto {
    private String username;
    private String password;
    private String ipAddress;
}
