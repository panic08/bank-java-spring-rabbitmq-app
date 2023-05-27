package ru.panic.template.endpoint;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.panic.template.dto.ProviderRequestDto;
@Endpoint
@RestController
@Slf4j
public class TestEndpoint {
    private static final String PROVIDER_NAMESPACE_URI = "http://localhost/ProviderEndpoint";

    @PayloadRoot(namespace = PROVIDER_NAMESPACE_URI, localPart = "ProviderRequest")
    @ResponsePayload
    private JAXBElement<ProviderRequestDto> helloRequest(@RequestPayload JAXBElement<ProviderRequestDto> request){

        return request;
    }
}
