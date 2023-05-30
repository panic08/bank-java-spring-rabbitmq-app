package ru.panic.template.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.panic.template.dto.enums.Currency;

@Entity
@Table(name = "p2p-transactions")
@Data
public class P2PTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String from_account;
    private String to_account;
    private Currency currency;
    private Double amount;
    private String description;
    private Long timestamp;
    private String sign;
}
