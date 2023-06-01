package ru.panic.template.repository;

import org.springframework.data.repository.CrudRepository;
import ru.panic.template.service.hash.WithdrawalSmsCodeVerifierHash;

public interface WithdrawalSmsCodeVerifierHashRepository extends CrudRepository<WithdrawalSmsCodeVerifierHash, String> {
}
