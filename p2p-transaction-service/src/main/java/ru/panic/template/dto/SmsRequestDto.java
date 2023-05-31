package ru.panic.template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class SmsRequestDto {
    private String to;
    private List<Messages> messages;
    @Data
    @AllArgsConstructor
    public static class Messages{
        private String channel;
        private String text;
    }
}
