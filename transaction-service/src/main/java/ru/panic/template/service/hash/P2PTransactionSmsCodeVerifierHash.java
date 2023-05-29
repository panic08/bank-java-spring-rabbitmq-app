package ru.panic.template.service.hash;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("p2pTransactionSmsCodeVerifiersHash")
@Data
public class P2PTransactionSmsCodeVerifierHash {
    @Id
    private String username;
    private String orderId;
    private Integer code;
    private Integer level;
    private Long timestamp;
}
