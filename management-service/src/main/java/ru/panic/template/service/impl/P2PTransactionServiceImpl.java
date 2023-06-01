package ru.panic.template.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.panic.template.dto.P2PTransactionRequest;
import ru.panic.template.entity.P2PTransaction;
import ru.panic.template.entity.User;
import ru.panic.template.repository.P2PTransactionRepository;
import ru.panic.template.repository.UserRepository;
import ru.panic.template.service.P2PTransactionService;

@Service
@Slf4j
public class P2PTransactionServiceImpl implements P2PTransactionService {
    public P2PTransactionServiceImpl(P2PTransactionRepository p2PTransactionRepository, UserRepository userRepository) {
        this.p2PTransactionRepository = p2PTransactionRepository;
        this.userRepository = userRepository;
    }

    private final P2PTransactionRepository p2PTransactionRepository;
    private final UserRepository userRepository;
    @Transactional
    @Override
    public void handleP2PTransaction(P2PTransactionRequest request) {
        if (request.getStatus() != 200) {
            return;
        }
        log.info("Handling p2p-transaction: {}", P2PTransactionRequest.class);
        User fromUser = userRepository.findByUsername(request.getFrom());
        User toUser = userRepository.findByUsername(request.getTo());
        switch (request.getCurrency()) {
            case RUB -> {
                if (fromUser.getRub_balance() < request.getAmount()) {
                    return;
                }
                fromUser.setRub_balance(fromUser.getRub_balance() - request.getAmount());
                toUser.setRub_balance(toUser.getRub_balance() + request.getAmount());

            }
            case USD -> {
                if (fromUser.getUsd_balance() < request.getAmount()) {
                    return;
                }
                fromUser.setUsd_balance(fromUser.getUsd_balance() - request.getAmount());
                toUser.setUsd_balance(toUser.getUsd_balance() + request.getAmount());
            }

            case EUR -> {
                if (fromUser.getEur_balance() < request.getAmount()) {
                    return;
                }
                fromUser.setEur_balance(fromUser.getEur_balance() - request.getAmount());
                toUser.setEur_balance(toUser.getEur_balance() + request.getAmount());
            }
        }
        userRepository.save(fromUser);
        userRepository.save(toUser);


        P2PTransaction p2PTransaction = new P2PTransaction();
        p2PTransaction.setFrom_account(request.getFrom());
        p2PTransaction.setTo_account(request.getTo());
        p2PTransaction.setAmount(request.getAmount());
        p2PTransaction.setCurrency(request.getCurrency());
        p2PTransaction.setDescription(request.getDesc());
        p2PTransaction.setTimestamp(request.getTimestamp());
        p2PTransaction.setSign(request.getSign());
        p2PTransactionRepository.save(p2PTransaction);
    }
}
