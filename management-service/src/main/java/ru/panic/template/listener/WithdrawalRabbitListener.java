package ru.panic.template.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.panic.template.dto.WithdrawalRequestDto;
import ru.panic.template.service.impl.WithdrawalServiceImpl;

@Component
@Slf4j
@RabbitListener(queues = "${spring.rabbitmq.queues.withdrawal-queue}")
public class WithdrawalRabbitListener {
    public WithdrawalRabbitListener(WithdrawalServiceImpl withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    private final WithdrawalServiceImpl withdrawalService;
    @RabbitHandler
    private void withdrawalReceive(String request){
        log.info("Receive message from Rabbitmq on {}", WithdrawalRabbitListener.class);
        ObjectMapper objectMapper = new ObjectMapper();
        WithdrawalRequestDto withdrawalRequestDto  = null;
        try {
            withdrawalRequestDto = objectMapper.readValue(request, WithdrawalRequestDto.class);
        } catch (JsonProcessingException jsonMappingException){
            log.warn("Bad json request on listener: {}", WithdrawalRabbitListener.class);
        }
        withdrawalService.handleWithdrawal(withdrawalRequestDto);

    }
}
