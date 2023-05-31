package ru.panic.template.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.panic.template.service.hash.P2PTransactionSmsCodeVerifierHash;
@Repository
public interface P2PTransactionSmsCodeVerifierHashRepository extends CrudRepository<P2PTransactionSmsCodeVerifierHash, String> {
}
