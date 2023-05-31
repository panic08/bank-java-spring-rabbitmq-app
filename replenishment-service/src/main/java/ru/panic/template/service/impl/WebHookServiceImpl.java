package ru.panic.template.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.panic.template.dto.factory.QiwiRequestDto;
import ru.panic.template.dto.ReplenishmentRequestDto;
import ru.panic.template.dto.enums.Currency;
import ru.panic.template.dto.enums.Method;
import ru.panic.template.service.WebHookService;
@Service
@Slf4j
public class WebHookServiceImpl implements WebHookService {
    public WebHookServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    private final RabbitTemplate rabbitTemplate;
    @Override
    public void handleQiwiWebhook(QiwiRequestDto request) {
        log.info("Starting handling webhook on method: handleQiwiWebhook");
        if (!request.getPayment().getStatus().equals("SUCCESS")){
            log.warn("Payment status has status: SUCCESS on method: handleQiwiWebhook");
            return;
        }
        ReplenishmentRequestDto requestDto = new ReplenishmentRequestDto();
        requestDto.setStatus(200);
        requestDto.setUsername(request.getPayment().getComment());
        requestDto.setAmount(request.getPayment().getTotal().getAmount());
        requestDto.setMethod(Method.QIWI);
        requestDto.setTimestamp(System.currentTimeMillis());
        switch (request.getPayment().getTotal().getCurrency()){
            case 643 -> requestDto.setCurrency(Currency.RUB);
            case 840 -> requestDto.setCurrency(Currency.USD);
            case 978 -> requestDto.setCurrency(Currency.EUR);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = null;
        try {
            jsonRequest = objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            log.warn("Bad jsonRequest: {}", QiwiRequestDto.class);
        }

        rabbitTemplate.convertAndSend("replenishment-queue", jsonRequest);
    }

}
