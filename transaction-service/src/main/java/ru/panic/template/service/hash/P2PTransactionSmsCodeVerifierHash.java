package ru.panic.template.service.hash;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import ru.panic.template.dto.enums.Currency;

@RedisHash("p2pTransactionSmsCodeVerifiersHash")
@Data
public class P2PTransactionSmsCodeVerifierHash {
    @Id
    private String username;
    private String orderId;
    private Integer code;
    private Integer level;
    private Long timestamp;
    private P2PTransaction P2PTransaction;
    @Data
    @AllArgsConstructor
    public static class P2PTransaction{
        private String from;
        private String to;
        private Currency currency;
        private Double amount;
        private String desc;
        private String sign;
    }
}
