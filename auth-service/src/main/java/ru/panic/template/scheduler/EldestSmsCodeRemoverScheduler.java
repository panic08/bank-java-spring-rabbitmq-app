package ru.panic.template.scheduler;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.panic.template.repository.AuthorizeSmsCodeVerifierHashRepository;
import ru.panic.template.service.hash.AuthorizeSmsCodeVerifierHash;

@Component
@Slf4j
public class EldestSmsCodeRemoverScheduler {
    public EldestSmsCodeRemoverScheduler(AuthorizeSmsCodeVerifierHashRepository authorizeSmsCodeVerifierHashRepository) {
        this.authorizeSmsCodeVerifierHashRepository = authorizeSmsCodeVerifierHashRepository;
    }

    private final AuthorizeSmsCodeVerifierHashRepository authorizeSmsCodeVerifierHashRepository;
    @Scheduled(fixedRate = 600000)
    public void eldestCodeRemover(){
        log.info("Deletion of auth-sms-codes-hash begins");
        List<AuthorizeSmsCodeVerifierHash> list =
                (List<AuthorizeSmsCodeVerifierHash>) authorizeSmsCodeVerifierHashRepository.findAll();
        list.forEach(h -> {
            if (System.currentTimeMillis() - h.getTimestamp() >= 600000){
                log.warn("Founded sms code verifier with timestamp >= 10 min");
                authorizeSmsCodeVerifierHashRepository.deleteById(h.getUsername());
            }
        });
    }
}
