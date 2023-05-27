package ru.panic.template.dto;

import lombok.Data;

@Data
public class SignUpRequestDto {
    private String username;
    private String password;
    private String ipAddress;
}
