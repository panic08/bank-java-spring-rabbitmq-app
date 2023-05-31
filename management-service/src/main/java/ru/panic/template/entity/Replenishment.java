package ru.panic.template.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.panic.template.dto.enums.Currency;
import ru.panic.template.dto.enums.Method;

@Entity
@Table(name = "replenishments")
@Data
public class Replenishment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private Double amount;
    private Currency currency;
    private Method method;
    private Long timestamp;
}
