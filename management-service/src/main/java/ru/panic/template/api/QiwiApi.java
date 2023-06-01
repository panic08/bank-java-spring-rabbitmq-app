package ru.panic.template.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.panic.template.dto.QiwiTransferRequestDto;

@Component
@Slf4j
public class QiwiApi {
    public QiwiApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${ru.panic.api.qiwi}")
    private static String QIWI_API_TOKEN;
    private static final String URI = "https://edge.qiwi.com/sinap/api/v2/terms/99/payments";
    private final RestTemplate restTemplate;
    public void sendTransfer(String number, Integer currency, Double amount){
        QiwiTransferRequestDto qiwiTransferRequestDto = new QiwiTransferRequestDto();
        qiwiTransferRequestDto.setId(11111L);
        qiwiTransferRequestDto.setComment("Withdrawal By");
        qiwiTransferRequestDto.setSum(new QiwiTransferRequestDto.Sum(amount, currency));
        qiwiTransferRequestDto.setFields(new QiwiTransferRequestDto.Fields(number));
        qiwiTransferRequestDto.setPaymentMethod(new QiwiTransferRequestDto.PaymentMethod("Account", currency));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = null;
        try {
            jsonRequest = objectMapper.writeValueAsString(qiwiTransferRequestDto);
        } catch (Exception e) {
            log.warn("Bad jsonRequest: {}", jsonRequest);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(QIWI_API_TOKEN);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(URI, HttpMethod.POST, requestEntity, String.class);
    }
}
