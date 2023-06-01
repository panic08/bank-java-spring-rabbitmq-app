package ru.panic.template.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.panic.template.entity.Withdrawal;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
}
