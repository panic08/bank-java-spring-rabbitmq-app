package ru.panic.template.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.panic.template.dto.P2PTransactionRequest;
import ru.panic.template.dto.P2PTransactionResponse;
import ru.panic.template.dto.ProviderRequestDto;
import ru.panic.template.dto.ProviderResponseDto;
import ru.panic.template.exception.InvalidCredentialsException;
import ru.panic.template.repository.P2PTransactionSmsCodeVerifierHashRepository;
import ru.panic.template.service.P2PTransactionService;
import ru.panic.template.service.hash.P2PTransactionSmsCodeVerifierHash;
import ru.panic.util.MD5Encryption;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class P2PTransactionServiceImpl implements P2PTransactionService {
    public P2PTransactionServiceImpl(RestTemplate restTemplate, RabbitTemplate rabbitTemplate, MD5Encryption md5Encryption, P2PTransactionSmsCodeVerifierHashRepository transactionSmsCodeVerifierHashRepository) {
        this.restTemplate = restTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.md5Encryption = md5Encryption;
        this.transactionSmsCodeVerifierHashRepository = transactionSmsCodeVerifierHashRepository;
    }

    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;

    private final MD5Encryption md5Encryption;
    private final P2PTransactionSmsCodeVerifierHashRepository transactionSmsCodeVerifierHashRepository;

    private static final String URL = "http://localhost:8080/api/v2/getInfoByJwt";
    @Override
    @Transactional
    public P2PTransactionResponse getTransaction(P2PTransactionRequest request) {
        List<P2PTransactionSmsCodeVerifierHash> list =
                (List<P2PTransactionSmsCodeVerifierHash>) transactionSmsCodeVerifierHashRepository.findAllById(Collections.singletonList(request.getFrom()));
        P2PTransactionSmsCodeVerifierHash hash = list.stream()
                .filter(p -> p.getOrderId().equals(request.getOrderId()))
                .toList()
                .get(0);

        if (!hash.getCode().equals(request.getCode())){
            throw new InvalidCredentialsException("Неверный смс код");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = null;

        try {
            jsonRequest = objectMapper.writeValueAsString(new ProviderRequestDto(request.getFrom()));
        } catch (Exception e) {
            log.warn("Bad jsonRequest: {}", request.getFrom());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);
        ResponseEntity<ProviderResponseDto> response = restTemplate.exchange(URL, HttpMethod.GET, requestEntity, ProviderResponseDto.class);

        switch (request.getCurrency()){
            case RUB -> {
                if (response.getBody().getRub_balance().doubleValue() < request.getAmount().doubleValue()){
                    throw new InvalidCredentialsException("Не хватает средств на балансе");
                }
            }
            case EUR -> {
                if (response.getBody().getEur_balance().doubleValue() < request.getAmount().doubleValue()){
                    throw new InvalidCredentialsException("Не хватает средств на балансе");
                }
            }
            case USD -> {
                if (response.getBody().getUsd_balance().doubleValue() < request.getAmount().doubleValue()){
                    throw new InvalidCredentialsException("Не хватает средств на балансе");
                }
            }
        }

        String signatureString = md5Encryption.encrypt(
                        request.getFrom() +
                        request.getTo() +
                        request.getAmount() +
                        request.getCurrency() +
                        request.getDesc());
        if(!signatureString.equals(request.getSign())){
            throw new InvalidCredentialsException("Неверный ключ подписи");
        }

        P2PTransactionResponse p2PTransactionResponse = new P2PTransactionResponse();
        p2PTransactionResponse.setStatus(200);
        p2PTransactionResponse.setFrom(request.getFrom());
        p2PTransactionResponse.setTo(request.getTo());
        p2PTransactionResponse.setDesc(request.getDesc());
        p2PTransactionResponse.setCurrency(request.getCurrency());
        p2PTransactionResponse.setTimestamp(System.currentTimeMillis());
        p2PTransactionResponse.setSign(request.getSign());

        String jsonRequest1 = null;
        try {
            jsonRequest1 = objectMapper.writeValueAsString(p2PTransactionResponse);
        } catch (Exception e) {
            log.warn("Bad jsonRequest: {}", p2PTransactionResponse);
        }

        rabbitTemplate.convertAndSend("p2p-transaction-queue", jsonRequest1);
        return p2PTransactionResponse;
    }
}
