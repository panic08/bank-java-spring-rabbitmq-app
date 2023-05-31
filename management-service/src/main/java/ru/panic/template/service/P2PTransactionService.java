package ru.panic.template.service;

import ru.panic.template.dto.P2PTransactionRequest;

public interface P2PTransactionService {
    void handleP2PTransaction(P2PTransactionRequest request);
}
