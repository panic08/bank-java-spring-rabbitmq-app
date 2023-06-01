package ru.panic.template.service.hash;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import ru.panic.template.dto.enums.Currency;
import ru.panic.template.dto.enums.Method;

@RedisHash("withdrawalSmsCodeVerifiersHash")
@Data
public class WithdrawalSmsCodeVerifierHash {
    @Id
    private String username;
    private String orderId;
    private Integer code;
    private Integer level;
    private Long timestamp;
    private Withdrawal withdrawal;
    @Data
    @AllArgsConstructor
    public static class Withdrawal{
        private String from;
        private String to;
        private Currency currency;
        private Double amount;
        private Method method;
        private String sign;
    }
}
