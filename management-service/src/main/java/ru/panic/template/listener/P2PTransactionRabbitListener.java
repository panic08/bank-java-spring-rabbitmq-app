package ru.panic.template.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.panic.template.dto.P2PTransactionRequest;
import ru.panic.template.service.impl.P2PTransactionServiceImpl;

@Component
@Slf4j
@RabbitListener(queues = "${spring.rabbitmq.queues.p2p-transaction-queue}")
public class P2PTransactionRabbitListener {
    public P2PTransactionRabbitListener(P2PTransactionServiceImpl p2PTransactionService) {
        this.p2PTransactionService = p2PTransactionService;
    }

    private final P2PTransactionServiceImpl p2PTransactionService;
    @RabbitHandler
    public void p2pTransactionReceive(String request){
        ObjectMapper objectMapper = new ObjectMapper();
        P2PTransactionRequest p2PTransactionRequest = null;
        try {
            p2PTransactionRequest = objectMapper.readValue(request, P2PTransactionRequest.class);
        } catch (JsonProcessingException jsonMappingException){
            log.warn("Bad json request on listener: {}", P2PTransactionRabbitListener.class);
        }

        p2PTransactionService.handleP2PTransaction(p2PTransactionRequest);
    }

}
