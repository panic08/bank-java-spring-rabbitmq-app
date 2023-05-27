package ru.panic.template.service.hash;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("authorizeSmsCodeVerifiersHash")
@Data
public class AuthorizeSmsCodeVerifierHash {
    @Id
    private String username;
    private Integer code;
    private Integer level;
    private Long timestamp;
}
