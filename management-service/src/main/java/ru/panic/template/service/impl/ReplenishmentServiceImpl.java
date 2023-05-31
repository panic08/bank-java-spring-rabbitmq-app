package ru.panic.template.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.panic.template.dto.ReplenishmentRequestDto;
import ru.panic.template.entity.Replenishment;
import ru.panic.template.entity.User;
import ru.panic.template.repository.ReplenishmentRepository;
import ru.panic.template.repository.UserRepository;
import ru.panic.template.service.ReplenishmentService;

@Service
@Slf4j
public class ReplenishmentServiceImpl implements ReplenishmentService {
    public ReplenishmentServiceImpl(ReplenishmentRepository replenishmentRepository, UserRepository userRepository) {
        this.replenishmentRepository = replenishmentRepository;
        this.userRepository = userRepository;
    }

    private final ReplenishmentRepository replenishmentRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public void handleReplenishment(ReplenishmentRequestDto request) {
        log.info("Handling replenishment: {}", ReplenishmentRequestDto.class);
        if (request.getStatus() != 200){
            return;
        }
        User user = userRepository.findByUsername(request.getUsername());

        switch (request.getCurrency()){
            case RUB -> user.setRub_balance(user.getRub_balance() + request.getAmount());
            case USD -> user.setUsd_balance(user.getUsd_balance() + request.getAmount());
            case EUR -> user.setEur_balance(user.getEur_balance() + request.getAmount());
        }

        userRepository.save(user);

        Replenishment replenishment = new Replenishment();
        replenishment.setUsername(request.getUsername());
        replenishment.setAmount(request.getAmount());
        replenishment.setCurrency(request.getCurrency());
        replenishment.setMethod(request.getMethod());
        replenishment.setTimestamp(request.getTimestamp());

        replenishmentRepository.save(replenishment);
    }
}
