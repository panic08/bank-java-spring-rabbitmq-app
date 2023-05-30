package ru.panic.template.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double rub_balance;
    private Double usd_balance;
    private Double eur_balance;
    private String username;
    private String password;
    private String ipAddress;
    private Boolean isNonLocked;
    private Boolean secure3D;
    private Long timestamp;

}
