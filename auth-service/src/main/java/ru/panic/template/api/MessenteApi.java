package ru.panic.template.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.panic.template.dto.SmsRequestDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Component
@Slf4j
public class MessenteApi {
    public MessenteApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Value("${ru.panic.messente.api.username}")
    private String MESSENTE_USERNAME;
    @Value("${ru.panic.messente.api.password}")
    private String MESSENTE_PASSWORD;
    private final String URL = "https://api.messente.com/v1/omnimessage";
    private final RestTemplate restTemplate;
    public void sendSms(String number, String message) {
        SmsRequestDto smsRequestDto = new SmsRequestDto();
        smsRequestDto.setTo(number);
        smsRequestDto.setMessages(new ArrayList<>(Collections.singletonList(new SmsRequestDto.Messages("sms", message))));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = null;
        try {
            jsonRequest = objectMapper.writeValueAsString(smsRequestDto);
        } catch (Exception e) {
            log.warn("Bad jsonRequest: {}", message);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(MESSENTE_USERNAME, MESSENTE_PASSWORD);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, String.class);
    }
}
