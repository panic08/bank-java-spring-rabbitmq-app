package ru.panic.template.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.panic.template.dto.ReplenishmentRequestDto;
import ru.panic.template.service.impl.ReplenishmentServiceImpl;

@Component
@Slf4j
@RabbitListener(queues = "${spring.rabbitmq.queues.replenishment-queue}")
public class ReplenishmentRabbitListener {
    public ReplenishmentRabbitListener(ReplenishmentServiceImpl replenishmentService) {
        this.replenishmentService = replenishmentService;
    }
    private final ReplenishmentServiceImpl replenishmentService;
    @RabbitHandler
    private void replenishmentReceive(String request){
        ObjectMapper objectMapper = new ObjectMapper();
        ReplenishmentRequestDto replenishmentRequestDto = new ReplenishmentRequestDto();
        try {
            replenishmentRequestDto = objectMapper.readValue(request, ReplenishmentRequestDto.class);
        } catch (JsonProcessingException jsonMappingException){
            log.warn("Bad json request on listener: {}", P2PTransactionRabbitListener.class);
        }

        replenishmentService.handleReplenishment(replenishmentRequestDto);
    }
}
