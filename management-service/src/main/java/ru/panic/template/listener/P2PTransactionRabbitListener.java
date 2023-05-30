package ru.panic.template.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RabbitListener(queues = "${spring.rabbitmq.queues.p2p-transaction-queue}")
public class P2PTransactionRabbitListener {
    @RabbitHandler
    private void p2pTransactionReceive(String request){
        System.out.println("I have a " + request);
    }

}
