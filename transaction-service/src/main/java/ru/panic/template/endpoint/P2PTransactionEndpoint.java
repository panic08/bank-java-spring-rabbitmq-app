package ru.panic.template.endpoint;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.panic.template.dto.P2PTransactionRequest;
import ru.panic.template.dto.P2PTransactionResponse;
import ru.panic.template.service.impl.P2PTransactionServiceImpl;

import javax.xml.namespace.QName;

@Endpoint
@RestController
@Slf4j
public class P2PTransactionEndpoint {
    public P2PTransactionEndpoint(P2PTransactionServiceImpl p2PTransactionService) {
        this.p2PTransactionService = p2PTransactionService;
    }

    private static final String P2P_TRANSACTION_NAMESPACE_URI = "http://localhost/P2PTransactionEndpoint";

    private final P2PTransactionServiceImpl p2PTransactionService;

    @PayloadRoot(namespace = P2P_TRANSACTION_NAMESPACE_URI, localPart = "P2PTransactionRequest")
    @ResponsePayload
    private JAXBElement<P2PTransactionResponse> p2pTransactionEndpoint(@RequestPayload JAXBElement<P2PTransactionRequest> request){
        return new JAXBElement<>(new QName(P2P_TRANSACTION_NAMESPACE_URI, "P2PTransactionResponse"), P2PTransactionResponse.class, p2PTransactionService.getTransaction(request.getValue()));
    }
}
