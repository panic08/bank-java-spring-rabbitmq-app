package ru.panic.template.service;

import ru.panic.template.dto.p2pTransaction.P2PPreTransactionRequest;
import ru.panic.template.dto.p2pTransaction.P2PTransactionRequest;
import ru.panic.template.dto.p2pTransaction.P2PTransactionResponse;

public interface P2PTransactionService {
    Object handleTransaction(P2PTransactionRequest request);
    P2PTransactionResponse handleSuccessTransaction(P2PPreTransactionRequest request);
}
