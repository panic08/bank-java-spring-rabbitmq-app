package ru.panic.template.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.panic.template.api.MessenteApi;
import ru.panic.template.dto.*;
import ru.panic.template.exception.InvalidCredentialsException;
import ru.panic.template.repository.WithdrawalSmsCodeVerifierHashRepository;
import ru.panic.template.service.WithdrawalService;
import ru.panic.template.service.hash.WithdrawalSmsCodeVerifierHash;
import ru.panic.util.CodeGeneratorUtil;
import ru.panic.util.MD5EncryptionUtil;
import ru.panic.util.OrderIdGeneratorUtil;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class WithdrawalServiceImpl implements WithdrawalService {
    public WithdrawalServiceImpl(RabbitTemplate rabbitTemplate, RestTemplate restTemplate, MD5EncryptionUtil md5Encryption, CodeGeneratorUtil codeGeneratorUtil, OrderIdGeneratorUtil orderIdGeneratorUtil, WithdrawalSmsCodeVerifierHashRepository withdrawalSmsCodeVerifierHashRepository, MessenteApi messenteApi) {
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate = restTemplate;
        this.md5Encryption = md5Encryption;
        this.codeGeneratorUtil = codeGeneratorUtil;
        this.orderIdGeneratorUtil = orderIdGeneratorUtil;
        this.withdrawalSmsCodeVerifierHashRepository = withdrawalSmsCodeVerifierHashRepository;
        this.messenteApi = messenteApi;
    }

    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;
    private final MD5EncryptionUtil md5Encryption;
    private final CodeGeneratorUtil codeGeneratorUtil;
    private final OrderIdGeneratorUtil orderIdGeneratorUtil;
    private final WithdrawalSmsCodeVerifierHashRepository withdrawalSmsCodeVerifierHashRepository;
    private final MessenteApi messenteApi;
    private static final String URL = "http://localhost:8080/api/v2/getInfoByJwt";
    @Override
    public WithdrawalResponseDto handleWithdrawal(WithdrawalRequestDto request) {
        ObjectMapper objectMapper = new ObjectMapper();
        log.info("Starting method: getTransaction with request: {}", WithdrawalRequestDto.class);
        String jsonRequest = null;

        try {
            jsonRequest = objectMapper.writeValueAsString(new ProviderRequestDto(request.getFrom()));
        } catch (JsonProcessingException e) {
            log.warn("Bad jsonRequest: {}", jsonRequest);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType(MediaType.APPLICATION_JSON));
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);
        ResponseEntity<ProviderResponseDto> response = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, ProviderResponseDto.class);

        switch (request.getCurrency()){
            case RUB -> {
                if (response.getBody().getRub_balance().doubleValue()<request.getAmount()){
                    throw new InvalidCredentialsException("Не хватает средств");
                }
            }
            case USD -> {
                if (response.getBody().getUsd_balance().doubleValue()<request.getAmount()){
                    throw new InvalidCredentialsException("Не хватает средств");
                }
            }
            case EUR -> {
                if (response.getBody().getEur_balance().doubleValue()<request.getAmount()){
                    throw new InvalidCredentialsException("Не хватает средств");
                }
            }
        }
        String signatureString = md5Encryption.encrypt(
                request.getFrom() +
                        request.getTo() +
                        request.getAmount() +
                        request.getCurrency() +
                        request.getMethod());
        if(!signatureString.equals(request.getSign())){
            throw new InvalidCredentialsException("Неверный ключ подписи");
        }

        //We calculate the commission 3%
        request.setAmount(request.getAmount()-(request.getAmount()*0.03));

        if(response.getBody().getSecure3D()){
            WithdrawalSmsCodeVerifierHash withdrawalSmsCodeVerifierHash = new WithdrawalSmsCodeVerifierHash();
            withdrawalSmsCodeVerifierHash.setUsername(request.getFrom());
            withdrawalSmsCodeVerifierHash.setCode(codeGeneratorUtil.generateRandomNumber());
            withdrawalSmsCodeVerifierHash.setLevel(0);
            withdrawalSmsCodeVerifierHash.setTimestamp(System.currentTimeMillis());
            withdrawalSmsCodeVerifierHash.setWithdrawal(new WithdrawalSmsCodeVerifierHash.Withdrawal(
                    response.getBody().getUsername(),
                    request.getTo(),
                    request.getCurrency(),
                    request.getAmount(),
                    request.getMethod(),
                    request.getSign()
            ));

            withdrawalSmsCodeVerifierHashRepository.save(withdrawalSmsCodeVerifierHash);

            WithdrawalResponseDto withdrawalResponseDto = new WithdrawalResponseDto();
            withdrawalResponseDto.setStatus(300);
            withdrawalResponseDto.setOrderId(withdrawalSmsCodeVerifierHash.getOrderId());
            withdrawalResponseDto.setFrom(response.getBody().getUsername());
            withdrawalResponseDto.setTo(request.getTo());
            withdrawalResponseDto.setCurrency(request.getCurrency());
            withdrawalResponseDto.setMethod(request.getMethod());
            withdrawalResponseDto.setAmount(request.getAmount());
            withdrawalResponseDto.setTimestamp(System.currentTimeMillis());
            withdrawalResponseDto.setSign(request.getSign());

            messenteApi.sendSms(response.getBody().getUsername(),
                    "Ваш код для подтверждения вывода (не говорите его никому): " + withdrawalSmsCodeVerifierHash.getCode());

            return withdrawalResponseDto;
        }

        WithdrawalResponseDto withdrawalResponseDto = new WithdrawalResponseDto();
        withdrawalResponseDto.setStatus(200);
        withdrawalResponseDto.setOrderId(orderIdGeneratorUtil.generateRandomCharacters(6));
        withdrawalResponseDto.setFrom(response.getBody().getUsername());
        withdrawalResponseDto.setTo(request.getTo());
        withdrawalResponseDto.setCurrency(request.getCurrency());
        withdrawalResponseDto.setMethod(request.getMethod());
        withdrawalResponseDto.setAmount(request.getAmount());
        withdrawalResponseDto.setTimestamp(System.currentTimeMillis());
        withdrawalResponseDto.setSign(request.getSign());

        String jsonRequest1 = null;
        try {
            jsonRequest1 = objectMapper.writeValueAsString(withdrawalResponseDto);
        } catch (JsonProcessingException e) {
            log.warn("Bad jsonRequest: {}", request.getFrom());
        }

        rabbitTemplate.convertAndSend("withdrawal-queue", jsonRequest1);
        return withdrawalResponseDto;
    }
    @Override
    public WithdrawalResponseDto handleSuccessWithdrawal(PreWithdrawalRequestDto request) {
        List<WithdrawalSmsCodeVerifierHash> list =
                (List<WithdrawalSmsCodeVerifierHash>) withdrawalSmsCodeVerifierHashRepository.findAllById(Collections.singletonList(request.getUsername()));
        WithdrawalSmsCodeVerifierHash hash = list.stream()
                .filter(p -> p.getOrderId().equals(request.getOrderId()))
                .toList()
                .get(0);

        if (hash.getCode().equals(request.getCode())){
            withdrawalSmsCodeVerifierHashRepository.delete(hash);
        }else{
            throw new InvalidCredentialsException("Неверный смс код");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        WithdrawalResponseDto withdrawalResponseDto = new WithdrawalResponseDto();
        withdrawalResponseDto.setStatus(200);
        withdrawalResponseDto.setOrderId(hash.getOrderId());
        withdrawalResponseDto.setFrom(hash.getWithdrawal().getFrom());
        withdrawalResponseDto.setTo(hash.getWithdrawal().getTo());
        withdrawalResponseDto.setCurrency(hash.getWithdrawal().getCurrency());
        withdrawalResponseDto.setMethod(hash.getWithdrawal().getMethod());
        withdrawalResponseDto.setAmount(hash.getWithdrawal().getAmount());
        withdrawalResponseDto.setTimestamp(System.currentTimeMillis());
        String signatureString1 = md5Encryption.encrypt(
                withdrawalResponseDto.getFrom() +
                        withdrawalResponseDto.getTo() +
                        withdrawalResponseDto.getAmount() +
                        withdrawalResponseDto.getCurrency() +
                        withdrawalResponseDto.getMethod() +
                        withdrawalResponseDto.getTimestamp()

        );
        withdrawalResponseDto.setSign(signatureString1);

        String jsonRequest1 = null;
        try {
            jsonRequest1 = objectMapper.writeValueAsString(withdrawalResponseDto);
        } catch (Exception e) {
            log.warn("Bad jsonRequest: {}", jsonRequest1);
        }

        rabbitTemplate.convertAndSend("withdrawal-queue", jsonRequest1);
        return withdrawalResponseDto;
    }
}
