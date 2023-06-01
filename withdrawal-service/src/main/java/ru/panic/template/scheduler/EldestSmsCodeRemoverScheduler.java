package ru.panic.template.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.panic.template.repository.WithdrawalSmsCodeVerifierHashRepository;
import ru.panic.template.service.hash.WithdrawalSmsCodeVerifierHash;

import java.util.List;

@Component
@Slf4j
public class EldestSmsCodeRemoverScheduler {
    public EldestSmsCodeRemoverScheduler(WithdrawalSmsCodeVerifierHashRepository repository) {
        this.repository = repository;
    }
    private final WithdrawalSmsCodeVerifierHashRepository repository;
    @Scheduled(fixedRate = 600000)
    public void eldestCodeRemover(){
        log.info("Deletion of auth-sms-codes-hash begins");
        List<WithdrawalSmsCodeVerifierHash> list = (List<WithdrawalSmsCodeVerifierHash>) repository.findAll();
        list.forEach(h -> {
            if (System.currentTimeMillis() - h.getTimestamp() >= 600000){
                log.warn("Founded sms code verifier with timestamp >= 10 min");
                repository.delete(h);
            }
        });
    }
}
