package ru.panic.template.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.panic.template.repository.P2PTransactionSmsCodeVerifierHashRepository;
import ru.panic.template.service.hash.P2PTransactionSmsCodeVerifierHash;

import java.util.List;

@Component
@Slf4j
public class EldestSmsCodeRemoverScheduler {
    public EldestSmsCodeRemoverScheduler(P2PTransactionSmsCodeVerifierHashRepository repository) {
        this.repository = repository;
    }

    private final P2PTransactionSmsCodeVerifierHashRepository repository;
    @Scheduled(fixedRate = 600000)
    public void eldestCodeRemover(){
        log.info("Deletion of auth-sms-codes-hash begins");
        List<P2PTransactionSmsCodeVerifierHash> list = (List<P2PTransactionSmsCodeVerifierHash>) repository.findAll();
        list.forEach(h -> {
            if (System.currentTimeMillis() - h.getTimestamp() >= 600000){
                log.warn("Founded sms code verifier with timestamp >= 10 min");
                repository.delete(h);
            }
        });
    }
}
