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
@Endpoint
@RestController
@Slf4j
public class P2PTransactionEndpoint {
    private static final String P2P_TRANSACTION_NAMESPACE_URI = "http://localhost/P2PTransactionEndpoint";

    @PayloadRoot(namespace = P2P_TRANSACTION_NAMESPACE_URI, localPart = "P2PTransactionRequest")
    @ResponsePayload
    private JAXBElement<P2PTransactionResponse> helloRequest(@RequestPayload JAXBElement<P2PTransactionRequest> request){

        return null;
    }
}
