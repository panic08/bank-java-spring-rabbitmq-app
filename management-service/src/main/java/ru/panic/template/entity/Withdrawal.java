package ru.panic.template.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.panic.template.dto.enums.Currency;
import ru.panic.template.dto.enums.Method;

@Entity
@Table(name = "withdrawals")
@Data
public class Withdrawal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String from_account;
    private String to_account;
    private Currency currency;
    private Method method;
    private Double amount;
    private Long timestamp;
    private String sign;
}
