package ru.panic.template.endpoint;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.panic.template.dto.AuthorizeRequestDto;
import ru.panic.template.dto.AuthorizeResponseDto;
import ru.panic.template.service.AuthorizeService;

import javax.xml.namespace.QName;

@Endpoint
@RestController
@Slf4j
public class AuthorizeEndpoint {
    public AuthorizeEndpoint(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService;
    }

    private final AuthorizeService authorizeService;
    private final String SIGN_IN_NAMESPACE_URI = "http://localhost/SignInEndpoint";
    private final String SIGN_UP_NAMESPACE_URI = "http://localhost/SignUpEndpoint";

    @PayloadRoot(namespace = SIGN_IN_NAMESPACE_URI, localPart = "SignInRequest")
    @ResponsePayload
    private JAXBElement<AuthorizeResponseDto> signIn(@RequestPayload JAXBElement<AuthorizeRequestDto> request){
        log.info("Received request for method: signIn");
        return new JAXBElement<>(new QName(SIGN_IN_NAMESPACE_URI, "SignInResponse"), AuthorizeResponseDto.class, authorizeService.signIn(request.getValue()));
    }
    @PayloadRoot(namespace = SIGN_UP_NAMESPACE_URI, localPart = "SignUpRequest")
    @ResponsePayload
    private JAXBElement<AuthorizeResponseDto> signUp(@RequestPayload JAXBElement<AuthorizeRequestDto> request){
        log.info("Received request for method: signUp");
        return new JAXBElement<>(new QName(SIGN_IN_NAMESPACE_URI, "SignUpResponse"), AuthorizeResponseDto.class, authorizeService.signUp(request.getValue()));
    }

}
