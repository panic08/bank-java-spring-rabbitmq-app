package ru.panic.template.dto;

import lombok.Data;

@Data
public class SignInRequestDto {
    private String username;
    private String password;
    private Integer code;
}
