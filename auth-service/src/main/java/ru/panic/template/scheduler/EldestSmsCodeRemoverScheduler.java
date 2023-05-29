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
        List<AuthorizeSmsCodeVerifierHash> authorizeSmsCodeVerifierHashList =
                (List<AuthorizeSmsCodeVerifierHash>) authorizeSmsCodeVerifierHashRepository.findAll();
        for (AuthorizeSmsCodeVerifierHash key : authorizeSmsCodeVerifierHashList){
            if (System.currentTimeMillis() - key.getTimestamp() >= 600000){
                log.warn("Founded sms code verifier with timestamp >= 10 min");
                authorizeSmsCodeVerifierHashRepository.deleteById(key.getUsername());
            }
        }
    }
}
