package ru.panic.template.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.panic.template.service.hash.AuthorizeSmsCodeVerifierHash;

@Repository
public interface AuthorizeSmsCodeVerifierHashRepository extends CrudRepository<AuthorizeSmsCodeVerifierHash, String> {

}
