package ru.panic.template.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.panic.template.api.QiwiApi;
import ru.panic.template.dto.WithdrawalRequestDto;
import ru.panic.template.entity.User;
import ru.panic.template.entity.Withdrawal;
import ru.panic.template.repository.UserRepository;
import ru.panic.template.repository.WithdrawalRepository;
import ru.panic.template.service.WithdrawalService;
@Service
@Slf4j
public class WithdrawalServiceImpl implements WithdrawalService {
    public WithdrawalServiceImpl(WithdrawalRepository withdrawalRepository, UserRepository userRepository, QiwiApi qiwiApi) {
        this.withdrawalRepository = withdrawalRepository;
        this.userRepository = userRepository;
        this.qiwiApi = qiwiApi;
    }

    private final WithdrawalRepository withdrawalRepository;
    private final UserRepository userRepository;
    private final QiwiApi qiwiApi;
    @Override
    @Transactional
    public void handleWithdrawal(WithdrawalRequestDto request) {
        log.info("Handling replenishment: {}", WithdrawalRequestDto.class);
        if(!request.getStatus().equals(200)){
            return;
        }
        User user = userRepository.findByUsername(request.getFrom());
        Integer currency = 0;

        switch (request.getCurrency()){
            case RUB -> {
                user.setRub_balance(user.getRub_balance()-request.getAmount());
                currency = 643;
            }
            case USD -> {
                user.setUsd_balance(user.getUsd_balance()-request.getAmount());
                currency = 840;
            }
            case EUR -> {
                user.setEur_balance(user.getEur_balance()-request.getAmount());
                currency = 978;
            }
        }
        userRepository.save(user);

        switch (request.getMethod()){
            case QIWI -> qiwiApi.sendTransfer(request.getTo(), currency, request.getAmount());
            case YOOMONEY -> {}
        }

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setFrom_account(request.getFrom());
        withdrawal.setTo_account(request.getTo());
        withdrawal.setAmount(request.getAmount());
        withdrawal.setCurrency(request.getCurrency());
        withdrawal.setMethod(request.getMethod());
        withdrawal.setTimestamp(request.getTimestamp());
        withdrawal.setSign(request.getSign());

        withdrawalRepository.save(withdrawal);

    }
}
