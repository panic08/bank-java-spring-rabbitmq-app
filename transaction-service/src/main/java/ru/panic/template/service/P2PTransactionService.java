package ru.panic.template.service;

import ru.panic.template.dto.P2PTransactionRequest;
import ru.panic.template.dto.P2PTransactionResponse;

public interface P2PTransactionService {
    P2PTransactionResponse getTransaction(P2PTransactionRequest request);
}
