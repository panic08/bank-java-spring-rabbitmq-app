package ru.panic.template.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.panic.template.entity.P2PTransaction;

@Repository
public interface P2PTransactionRepository extends JpaRepository<P2PTransaction, Long> {
}
