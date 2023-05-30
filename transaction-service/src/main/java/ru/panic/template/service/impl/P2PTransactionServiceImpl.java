package ru.panic.template.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.panic.template.dto.*;
import ru.panic.template.dto.p2pTransaction.P2PPreTransactionRequest;
import ru.panic.template.dto.p2pTransaction.P2PPreTransactionResponse;
import ru.panic.template.dto.p2pTransaction.P2PTransactionRequest;
import ru.panic.template.dto.p2pTransaction.P2PTransactionResponse;
import ru.panic.template.exception.InvalidCredentialsException;
import ru.panic.template.repository.P2PTransactionSmsCodeVerifierHashRepository;
import ru.panic.template.service.P2PTransactionService;
import ru.panic.template.service.hash.P2PTransactionSmsCodeVerifierHash;
import ru.panic.util.MD5Encryption;

import java.util.Collections;
import java.util.List;

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
    public Object handleTransaction(P2PTransactionRequest request) {
//        List<P2PTransactionSmsCodeVerifierHash> list =
//                (List<P2PTransactionSmsCodeVerifierHash>) transactionSmsCodeVerifierHashRepository.findAllById(Collections.singletonList(request.getFrom()));
//        P2PTransactionSmsCodeVerifierHash hash = list.stream()
//                .filter(p -> p.getOrderId().equals(request.getOrderId()))
//                .toList()
//                .get(0);
//
//        if (hash.getCode().equals(request.getCode())){
//            transactionSmsCodeVerifierHashRepository.delete(hash);
//        }else{
//            throw new InvalidCredentialsException("Неверный смс код");
//        }

        log.info("Starting method: getTransaction with request: {}", P2PTransactionRequest.class);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = null;

        try {
            jsonRequest = objectMapper.writeValueAsString(new ProviderRequestDto(request.getFrom()));
        } catch (Exception e) {
            log.warn("Bad jsonRequest: {}", request.getFrom());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType(MediaType.APPLICATION_JSON));
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);
        ResponseEntity<ProviderResponseDto> response = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, ProviderResponseDto.class);


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
        System.out.println(signatureString);
        if(!signatureString.equals(request.getSign())){
            throw new InvalidCredentialsException("Неверный ключ подписи");
        }
        //Нам не нужно проверять на существующие смс, клинер все зачистит
        if(response.getBody().getSecure3D()){
            P2PTransactionSmsCodeVerifierHash hash = new P2PTransactionSmsCodeVerifierHash();
            hash.setUsername(request.getFrom());
            //YES
            hash.setOrderId("");
            hash.setLevel(0);
            hash.setTimestamp(System.currentTimeMillis());
            hash.setCode(0000);
            hash.setP2PTransaction(new P2PTransactionSmsCodeVerifierHash.P2PTransaction(
                    response.getBody().getUsername(),
                    request.getFrom(),
                    request.getCurrency(),
                    request.getAmount(),
                    request.getDesc(),
                    request.getSign()
            ));
            transactionSmsCodeVerifierHashRepository.save(hash);
            return new P2PPreTransactionResponse(200, hash.getOrderId(), hash.getUsername());
        }

        P2PTransactionResponse p2PTransactionResponse = new P2PTransactionResponse();
        p2PTransactionResponse.setStatus(200);
        p2PTransactionResponse.setFrom(response.getBody().getUsername());
        p2PTransactionResponse.setAmount(request.getAmount());
        p2PTransactionResponse.setTo(request.getTo());
        p2PTransactionResponse.setDesc(request.getDesc());
        p2PTransactionResponse.setCurrency(request.getCurrency());
        p2PTransactionResponse.setTimestamp(System.currentTimeMillis());
        String signatureString1 = md5Encryption.encrypt(
                p2PTransactionResponse.getFrom() +
                        p2PTransactionResponse.getTo() +
                        p2PTransactionResponse.getAmount() +
                        p2PTransactionResponse.getCurrency() +
                        p2PTransactionResponse.getDesc() +
                        p2PTransactionResponse.getTimestamp()
        );
        p2PTransactionResponse.setSign(signatureString1);

        String jsonRequest1 = null;
        try {
            jsonRequest1 = objectMapper.writeValueAsString(p2PTransactionResponse);
        } catch (Exception e) {
            log.warn("Bad jsonRequest: {}", p2PTransactionResponse);
        }


        rabbitTemplate.convertAndSend("p2p-transaction-queue", jsonRequest1);
        return p2PTransactionResponse;
    }

    @Override
    public P2PTransactionResponse handleSuccessTransaction(P2PPreTransactionRequest request) {
                List<P2PTransactionSmsCodeVerifierHash> list =
                (List<P2PTransactionSmsCodeVerifierHash>) transactionSmsCodeVerifierHashRepository.findAllById(Collections.singletonList(request.getUsername()));
        P2PTransactionSmsCodeVerifierHash hash = list.stream()
                .filter(p -> p.getOrderId().equals(request.getOrderId()))
                .toList()
                .get(0);

        if (hash.getCode().equals(request.getCode())){
            transactionSmsCodeVerifierHashRepository.delete(hash);
        }else{
            throw new InvalidCredentialsException("Неверный смс код");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        P2PTransactionResponse p2PTransactionResponse = new P2PTransactionResponse();
        p2PTransactionResponse.setStatus(200);
        p2PTransactionResponse.setFrom(hash.getP2PTransaction().getFrom());
        p2PTransactionResponse.setAmount(hash.getP2PTransaction().getAmount());
        p2PTransactionResponse.setTo(hash.getP2PTransaction().getTo());
        p2PTransactionResponse.setDesc(hash.getP2PTransaction().getDesc());
        p2PTransactionResponse.setCurrency(hash.getP2PTransaction().getCurrency());
        p2PTransactionResponse.setTimestamp(hash.getTimestamp());
        String signatureString1 = md5Encryption.encrypt(
                p2PTransactionResponse.getFrom() +
                        p2PTransactionResponse.getTo() +
                        p2PTransactionResponse.getAmount() +
                        p2PTransactionResponse.getCurrency() +
                        p2PTransactionResponse.getDesc() +
                        p2PTransactionResponse.getTimestamp()

        );
        p2PTransactionResponse.setSign(signatureString1);

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
